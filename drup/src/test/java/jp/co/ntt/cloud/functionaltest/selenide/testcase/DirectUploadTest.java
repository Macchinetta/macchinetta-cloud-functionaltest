/*
 * Copyright 2014-2018 NTT Corporation.
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

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.IndexPage;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class DirectUploadTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    private AmazonS3 s3Client;

    @Value("${bucketName}")
    private String bucketName;

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

        // バケット初期化
        cleanBucket("0000000001");

        // テスト実行:ログイン後ファイルをアップロードする。
        String filePath = "src/test/resources/files/Liberty.jpg";
        File uploadFile = new File(filePath);

        IndexPage indexPage = open(applicationContextUrl, LoginPage.class)
                .login("0000000001", "aaaaa11111");

        indexPage.upload(uploadFile);

        // サスペンド:アップロード成功確認
        indexPage.getMessage().shouldHave(exactText("アップロードに成功しました。"));

        // アップロードされたファイルを取得
        ObjectListing listObjects = s3Client.listObjects(bucketName);
        boolean existed = false;
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            String objKey = summary.getKey();
            S3Object obj = s3Client.getObject(bucketName, objKey);
            InputStream is = obj.getObjectContent();
            File remoteFile = new File("./", "dummy.dat");
            Files.copy(is, remoteFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            is.close();

            // ファイルの比較、メタデータの確認
            byte[] b1 = Files.readAllBytes(uploadFile.toPath());
            byte[] b2 = Files.readAllBytes(remoteFile.toPath());
            remoteFile.delete();
            if (Arrays.equals(b1, b2) && "Liberty.jpg".equals(obj
                    .getObjectMetadata().getUserMetaDataOf("filename"))) {
                existed = true;

                // 後始末
                s3Client.deleteObject(bucketName, objKey);
                break;
            }
        }

        // アサート:アップロードに使用したファイルと、アップロード後にS3バケットからダウンロードしたファイルを比較し、同一であることを確認する
        // アサート:S3にアップロードされたファイルに、APで付加したメタデータが付与されていることを確認する。
        assertTrue(existed);

        // 証跡取得
        screenshot("uploadTest01");
    }

    /**
     * RDRP0102 001 ファイルのダイレクトアップロードが、ポリシー違反により失敗することを確認する
     * @throws IOException
     */
    @Test
    public void uploadTest02() throws IOException {

        // バケット初期化
        cleanBucket("0000000001");

        // テスト実行:ログイン後ファイルをアップロードする。
        String filePath = "src/test/resources/files/Napoleon.jpg";
        File uploadFile = new File(filePath);

        IndexPage indexPage = open(applicationContextUrl, LoginPage.class)
                .login("0000000001", "aaaaa11111");

        indexPage.upload(uploadFile);

        // サスペンド:アップロード失敗確認
        indexPage.getMessage().shouldHave(exactText(
                "アップロードできるファイルは819200バイトまでです。"));

        // アップロードされていないことを確認
        ObjectListing listObjects = s3Client.listObjects(bucketName);
        boolean existed = false;
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            String objKey = summary.getKey();
            S3Object obj = s3Client.getObject(bucketName, objKey);
            InputStream is = obj.getObjectContent();
            File remoteFile = new File("./", "dummy.dat");
            Files.copy(is, remoteFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            is.close();

            // ファイルの比較、メタデータの確認
            byte[] b1 = Files.readAllBytes(uploadFile.toPath());
            byte[] b2 = Files.readAllBytes(remoteFile.toPath());
            remoteFile.delete();
            if (Arrays.equals(b1, b2) && "Napoleon.jpg".equals(obj
                    .getObjectMetadata().getUserMetaDataOf("filename"))) {
                existed = true;
                break;
            }
        }

        // アサート:アップロードが失敗し、エラーコード"EntityTooLarge"が返却されることを確認する
        assertFalse(existed);

        // 証跡取得
        screenshot("uploadTest02");
    }

    /**
     * RDRP0103 001 ファイルのダイレクトアップロードが、ポリシー有効期間切れにより失敗することを確認する
     * @throws IOException
     */
    @Test
    public void uploadTest03() throws IOException {

        // バケット初期化
        cleanBucket("0000000001");

        // テスト実行:ログイン後ファイルをアップロードする。
        String filePath = "src/test/resources/files/Liberty.jpg";
        File uploadFile = new File(filePath);

        IndexPage indexPage = open(applicationContextUrl, LoginPage.class)
                .login("0000000001", "aaaaa11111");

        indexPage.uploadWithDelay(uploadFile);

        // サスペンド:アップロード失敗確認
        indexPage.getMessage().shouldHave(exactText("アップロードに失敗しました。"));

        // アップロードされていないことを確認
        ObjectListing listObjects = s3Client.listObjects(bucketName);
        boolean existed = false;
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            String objKey = summary.getKey();
            S3Object obj = s3Client.getObject(bucketName, objKey);
            InputStream is = obj.getObjectContent();
            File remoteFile = new File("./", "dummy.dat");
            Files.copy(is, remoteFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            is.close();

            // ファイルの比較、メタデータの確認
            byte[] b1 = Files.readAllBytes(uploadFile.toPath());
            byte[] b2 = Files.readAllBytes(remoteFile.toPath());
            remoteFile.delete();
            if (Arrays.equals(b1, b2) && "Napoleon.jpg".equals(obj
                    .getObjectMetadata().getUserMetaDataOf("filename"))) {
                existed = true;
                break;
            }
        }

        // アサート:アップロードが失敗し、エラーコード"AccessDenied"が返却されることを確認する
        assertFalse(existed);

        // 証跡取得
        screenshot("uploadTest03");
    }

    @PostConstruct
    private void createS3Client() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    private void cleanBucket(String userId) {
        ObjectListing listObjects = s3Client.listObjects(bucketName, userId
                + "/");
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            String objKey = summary.getKey();
            s3Client.deleteObject(bucketName, objKey);
        }
    }

}
