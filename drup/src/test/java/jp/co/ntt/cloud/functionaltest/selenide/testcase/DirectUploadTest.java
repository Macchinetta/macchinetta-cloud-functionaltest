/*
 * Copyright(c) 2017 NTT Corporation.
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
 */
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

import java.io.File;
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

import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.UploadPage;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class DirectUploadTest extends TestCase {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * アプリケーションURL
     */
    @Value("${path.report}")
    private String reportPath;

    /*
     * ユーザID
     */
    private String userId;

    /*
     * パスワード
     */
    private String password;

    /*
     * S3クライアント
     */
    private AmazonS3 s3Client;

    /*
     * バケット名
     */
    @Value("${bucketName}")
    private String bucketName;

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
        Configuration.timeout = 10000;
    }

    @After
    public void tearDown() {
        open(applicationContextUrl, UploadPage.class).logout();
    }

    /*
     * S3にファイルがアップロードされることを確認する。
     */
    @Test
    public void uploadTest01() throws Exception {

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";

        // バケット初期化
        cleanBucket(userId);

        // テスト実行
        String filePath = "src/test/resources/files/Liberty.jpg";
        File uploadFile = new File(filePath);

        // @formatter:off
        open(applicationContextUrl, TopPage.class).login(userId, password)
                .upload(uploadFile);
        // @formatter:on

        // アップロード成功確認
        $("#message").shouldHave(text("アップロードに成功しました。"));

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
        assertTrue(existed);

        // 証跡取得
        screenshot("uploadTest01");
    }

    private void cleanBucket(String userId) {
        ObjectListing listObjects = s3Client.listObjects(bucketName, userId
                + "/");
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            String objKey = summary.getKey();
            s3Client.deleteObject(bucketName, objKey);
        }
    }

    /*
     * ファイルサイズ上限にてアップロードが失敗することを確認する。
     */
    @Test
    public void uploadTest02() throws Exception {

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";

        // バケット初期化
        cleanBucket(userId);

        // テスト実行
        String filePath = "src/test/resources/files/Napoleon.jpg";
        File uploadFile = new File(filePath);

        // @formatter:off
        open(applicationContextUrl, TopPage.class).login(userId, password)
                .upload(uploadFile);
        // @formatter:on

        // アップロード失敗確認
        $("#message").shouldHave(text("アップロードできるファイルは819200バイトまでです。"));

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
        assertFalse(existed);

        // 証跡取得
        screenshot("uploadTest02");
    }

    /*
     * ポリシー文書有効期限切れにてアップロードが失敗することを確認する。
     */
    @Test
    public void uploadTest03() throws Exception {

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";

        // バケット初期化
        cleanBucket(userId);

        // テスト実行
        String filePath = "src/test/resources/files/Liberty.jpg";
        File uploadFile = new File(filePath);

        // @formatter:off
        open(applicationContextUrl, TopPage.class).login(userId, password)
                .uploadWithDelay(uploadFile);
        // @formatter:on

        // アップロード失敗確認
        $("#message").shouldHave(text("アップロードに失敗しました。"));

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
        assertFalse(existed);

        // 証跡取得
        screenshot("uploadTest03");
    }

    @PostConstruct
    private void createS3Client() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }

}
