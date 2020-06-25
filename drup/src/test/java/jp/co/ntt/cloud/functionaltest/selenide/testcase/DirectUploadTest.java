/*
 * Copyright 2014-2020 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.IndexPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class DirectUploadTest {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    private AmazonS3 s3Client;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${prefix}")
    private String prefix;

    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // 検証メソッドタイムアウトの設定
        Configuration.timeout = 10000;

        // ポーリング間隔の設定
        Configuration.pollingInterval = 500;
    }

    @After
    public void tearDown() {
        open(applicationContextUrl, IndexPage.class).logout();
    }

    /**
     * RDRP0101 001 ファイルのダイレクトアップロードが行える事を確認する
     * @throws IOException
     */
    @Test
    public void uploadTest01() throws IOException {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // バケット初期化
                cleanBucket("0000000001");

                // 暫定対応:#33の解析のため
                assertTrue(String.format(
                        "bucketName[%s] userId[%s] prefix[%s]を初期化しましたが空になっていません。",
                        bucketName, "0000000001", prefix), isEmptyBucket(
                                "0000000001"));

                // テスト実行:ログイン後ファイルをアップロードする。
                final File uploadFile = new File("src/test/resources/files/Liberty.jpg");

                IndexPage indexPage = open(applicationContextUrl,
                        LoginPage.class).login("0000000001", "aaaaa11111");

                indexPage.upload(uploadFile);

                // サスペンド:アップロード成功確認
                indexPage.getMessage().shouldHave(exactText("アップロードに成功しました。"));

                // アップロードされたファイルを取得
                final S3Object obj = findObject("Liberty.jpg");

                // 暫定対応:#33の解析のため
                assertNotNull(String.format(
                        "[%s]がS3上にありません。 bucketName[%s] prefix[%s]",
                        "Liberty.jpg", bucketName, prefix), obj);

                try (InputStream is = obj.getObjectContent()) {
                    // アサート:アップロードに使用したファイルと、アップロード後にS3バケットからダウンロードしたファイルを比較し、同一であることを確認する
                    assertArrayEquals(
                            "アップロード元ファイルとS3バケット内のリモートファイルのバイト列が一致しません。", Files
                                    .readAllBytes(uploadFile.toPath()), IOUtils
                                            .toByteArray(is));
                } finally {
                    s3Client.deleteObject(bucketName, obj.getKey());
                }

                // 証跡取得
                screenshot("uploadTest01");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * RDRP0102 001 ファイルのダイレクトアップロードが、ポリシー違反により失敗することを確認する
     * @throws IOException
     */
    @Test
    public void uploadTest02() throws IOException {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // バケット初期化
                cleanBucket("0000000001");

                assertTrue(String.format(
                        "bucketName[%s] userId[%s] prefix[%s]を初期化しましたが空になっていません。",
                        bucketName, "0000000001", prefix), isEmptyBucket(
                                "0000000001"));

                // テスト実行:ログイン後ファイルをアップロードする。
                final File uploadFile = new File("src/test/resources/files/Napoleon.jpg");

                final IndexPage indexPage = open(applicationContextUrl,
                        LoginPage.class).login("0000000001", "aaaaa11111");

                indexPage.upload(uploadFile);

                // サスペンド:アップロード失敗確認
                // アサート:アップロードが失敗し、エラーコード"EntityTooLarge"が返却されることを確認する
                indexPage.getMessage().shouldHave(exactText(
                        "アップロードできるファイルは819200バイトまでです。"));

                // アップロードされていないことを確認
                final S3Object obj = findObject("Napoleon.jpg");

                // アサート:アップロードが失敗し、エラーコード"EntityTooLarge"が返却されることを確認する
                assertNull(String.format(
                        "アップロードに失敗しているにもかかわらず、[%s]がbucketName[%s] prefix[%s]にあります。",
                        "Napoleon.jpg", bucketName, prefix), obj);

                // 証跡取得
                screenshot("uploadTest02");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * RDRP0103 001 ファイルのダイレクトアップロードが、ポリシー有効期間切れにより失敗することを確認する
     * @throws IOException
     */
    @Test
    public void uploadTest03() throws IOException {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // バケット初期化
                cleanBucket("0000000001");

                // 暫定対応:#33の解析のため
                assertTrue(String.format(
                        "bucketName[%s] userId[%s] prefix[%s]を初期化しましたが空になっていません。",
                        bucketName, "0000000001", prefix), isEmptyBucket(
                                "0000000001"));

                // テスト実行:ログイン後ファイルをアップロードする。
                final File uploadFile = new File("src/test/resources/files/Liberty.jpg");

                final IndexPage indexPage = open(applicationContextUrl,
                        LoginPage.class).login("0000000001", "aaaaa11111");

                final StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                indexPage.uploadWithDelay(uploadFile);

                // サスペンド:アップロード失敗確認
                String message = indexPage.getMessage().waitUntil(appears,
                        10000L).text();
                stopWatch.stop();

                assertThat("ブラウザで5秒待機しているはずですが、4秒以内に帰ってきています", stopWatch
                        .getTotalTimeMillis(), greaterThan(4000L));

                // アサート:アップロードが失敗することを確認する
                if (!"アップロードに失敗しました。".equals(message)) {
                    // 暫定対応:#33の解析のため
                    System.err.println(String.format("結果メッセージ[%s]", message));
                    final S3Object obj = findObject("Liberty.jpg");
                    String s3State = "";
                    String metadata = "";
                    if (obj != null) {
                        s3State = String.format(
                                "#### S3上のオブジェクト状態 objectKey[%s] bucketName[%s]",
                                obj.getKey(), obj.getBucketName());
                        ObjectMetadata om = obj.getObjectMetadata();
                        metadata = String.format(
                                "     metadata:%s          expirationTime[%s]  fileName[%s] contentLength[%d] contentType[%s] lastModified[%s] etag[%s]",
                                System.lineSeparator(), om.getExpirationTime(),
                                om.getUserMetaDataOf("filename"), om
                                        .getContentLength(), om
                                                .getContentType(), om
                                                        .getLastModified(), om
                                                                .getETag());
                        s3Client.deleteObject(bucketName, obj.getKey());
                    }
                    // 暫定対応:#33の解析のため
                    fail(String.format(
                            "有効期限を超過していますが、アップロードに失敗しませんでした。S3の状態[%s], metadata[%s]",
                            s3State, metadata));
                }

                // アップロードされていないことを確認
                final S3Object obj = findObject("Liberty.jpg");

                assertNull(String.format(
                        "アップロードに失敗しているにもかかわらず、[%s]がbucketName[%s] prefix[%s]にあります。",
                        "Liberty.jpg", bucketName, prefix), obj);

                // 証跡取得
                screenshot("uploadTest03");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }

    @PostConstruct
    private void createS3Client() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    private void cleanBucket(String userId) {
        final ObjectListing listObjects = s3Client.listObjects(bucketName,
                prefix + userId + "/");
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            String objKey = summary.getKey();
            s3Client.deleteObject(bucketName, objKey);
        }
    }

    private boolean isEmptyBucket(String userId) {
        return s3Client.listObjects(bucketName, String.format("%s%s/", prefix,
                userId)).getObjectSummaries().isEmpty();
    }

    private S3Object findObject(String fileName) {
        assertNotNull(fileName);
        final ObjectListing listObjects = s3Client.listObjects(bucketName,
                prefix);
        if (listObjects == null) {
            return null;
        }
        S3Object s3Object = null;
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            final S3Object object = s3Client.getObject(bucketName, summary
                    .getKey());
            if (fileName.equals(object.getObjectMetadata().getUserMetaDataOf(
                    "filename"))) {
                s3Object = object;
                break;
            }
        }
        return s3Object;
    }
}
