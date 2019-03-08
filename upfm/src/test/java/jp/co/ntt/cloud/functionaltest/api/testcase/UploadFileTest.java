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
package jp.co.ntt.cloud.functionaltest.api.testcase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class UploadFileTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * バケット名
     */
    @Value("${bucketName}")
    private String bucketName;

    /*
     * S3クライアント
     */
    private AmazonS3 s3Client;

    /*
     * s3バケット内のオブジェクト（tempディレクトリ配下）を削除する。 削除後はディレクトリも削除されるため、新たにtempディレクトリを追加する。
     */
    @After
    public void tearDown() {
        ObjectListing list = s3Client.listObjects(bucketName, "temp/");
        List<KeyVersion> keys = new ArrayList<KeyVersion>();
        if (list.getObjectSummaries() != null) {
            for (S3ObjectSummary s : list.getObjectSummaries()) {
                keys.add(new KeyVersion(s.getKey()));
            }
            DeleteObjectsRequest request2 = new DeleteObjectsRequest(bucketName);
            request2.setKeys(keys);
            s3Client.deleteObjects(request2);
            s3Client.putObject(bucketName, "temp/", "");
        }
    }

    /**
     * UPFM0101001<br>
     * ファイルのアップロードが行える事を確認する。
     */
    @Test
    public void testUpload() throws Exception {

        File uploadFile = new File("src/test/resources/files/Liberty.jpg");

        // @formatter:off
        String response = given().multiPart("file", uploadFile).when().post(
                applicationContextUrl + "api/").asString();
        // @formatter:on

        // ファイルがアップロードされたことの確認
        // 引用符を削除
        String objectKey = response.substring(1, response.length() - 1);
        S3Object remoteObject = s3Client.getObject(bucketName, objectKey);
        InputStream is = remoteObject.getObjectContent();

        File remoteFile = new File("./", "dummy.dat");
        Files.copy(is, remoteFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        is.close();

        byte[] b1 = Files.readAllBytes(uploadFile.toPath());
        byte[] b2 = Files.readAllBytes(remoteFile.toPath());
        remoteFile.delete();
        assertTrue(Arrays.equals(b1, b2));
    }

    /**
     * UPFM0102001<br>
     * 単一ファイルの削除が行える事を確認する。
     */
    @Test
    public void testDelete() throws Exception {

        // 事前準備
        File uploadFile = new File("src/test/resources/files/Gleaners.jpg");

        String objectKey = "temp/deleteTestFile_" + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey, uploadFile);

        assertTrue(s3Client.doesObjectExist(bucketName, objectKey));

        // @formatter:off
        given().delete(applicationContextUrl + "api?objectkey=" + objectKey);
        // @formatter:on

        assertFalse(s3Client.doesObjectExist(bucketName, objectKey));
    }

    /**
     * UPFM0103001<br>
     * 複数ファイルの削除が行える事を確認する。
     */
    @Test
    public void testMultiDelete() throws Exception {

        // 事前準備
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        File uploadFile2 = new File("src/test/resources/files/Gleaners.jpg");

        String objectKey1 = "temp/deleteTestFile_" + UUID.randomUUID();
        String objectKey2 = "temp/deleteTestFile_" + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey1, uploadFile1);
        s3Client.putObject(bucketName, objectKey2, uploadFile2);

        assertTrue(s3Client.doesObjectExist(bucketName, objectKey1));
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey2));

        // @formatter:off
        given().delete(applicationContextUrl + "api?objectkeys=" + objectKey1
                + "&objectkeys=" + objectKey2);
        // @formatter:on

        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey2));
    }

    /**
     * UPFM0104001<br>
     * ファイル検索が行える事を確認する。
     */
    @Test
    public void testSearch() throws Exception {

        // 事前準備
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        File uploadFile2 = new File("src/test/resources/files/Gleaners.jpg");

        String objectKey1 = "temp/searchTestFile_" + UUID.randomUUID();
        String objectKey2 = "temp/searchTestFile_" + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey1, uploadFile1);
        s3Client.putObject(bucketName, objectKey2, uploadFile2);

        assertTrue(s3Client.doesObjectExist(bucketName, objectKey1));
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey2));

        String pattern = "temp/searchTestFile*";
        // @formatter:off
        String[] searchResult = given().get(applicationContextUrl
                + "api?pattern=" + pattern).as(String[].class);
        List<String> list = Arrays.asList(searchResult);
        System.out.println("test" + searchResult[0]);
        // @formatter:on

        String[] expectedArray = { objectKey1, objectKey2 };

        assertThat(list, hasItems(expectedArray));
    }

    /**
     * UPFM0105001<br>
     * S3上に存在しないオブジェクトキーを指定して単一ファイルの削除をしても例外が発生せず、そのままAPIがリターンすること。
     */
    @Test
    public void testDeleteNonExistObject() {
        String objectKey1 = "temp/searchTestFile_" + UUID.randomUUID();
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));

        int status = given().delete(applicationContextUrl + "api?objectkey="
                + objectKey1).getStatusCode();
        assertThat(status, is(200));
    }

    /**
     * UPFM0105002<br>
     * S3上に存在しないオブジェクトキーを指定して複数ファイルの削除をしても例外が発生せず、そのままAPIがリターンすること。
     */
    @Test
    public void testDeleteNonExistObjects() {
        String objectKey1 = "temp/searchTestFile_" + UUID.randomUUID();
        String objectKey2 = "temp/searchTestFile_" + UUID.randomUUID();

        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey2));

        int status = given().delete(applicationContextUrl + "api?objectkeys="
                + objectKey1 + "&objectkeys=" + objectKey2).getStatusCode();

        assertThat(status, is(200));
    }

    /**
     * UPFM0105003<br>
     * S3上に存在・非存在が混在しているオブジェクトキーを指定して複数ファイルの削除をしても例外が発生せず、S3上に存在するオブジェクトが削除されていること。
     */
    @Test
    public void testDeleteExistAndNonExistObjects() {

        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");

        String objectKey1 = "temp/searchTestFile_" + UUID.randomUUID();
        String objectKey2 = "temp/searchTestFile_" + UUID.randomUUID();

        // put only objectKey1
        s3Client.putObject(bucketName, objectKey1, uploadFile1);

        assertTrue(s3Client.doesObjectExist(bucketName, objectKey1));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey2));

        int status = given().delete(applicationContextUrl + "api?objectkeys="
                + objectKey1 + "&objectkeys=" + objectKey2).getStatusCode();

        assertThat(status, is(200));

        // confirm delete file by objectkey1
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));
    }

    /**
     * UPFM0106001<br>
     * S3上に存在しないオブジェクトキーパターンを指定してファイル検索を行うと、検索条件にマッチする配列が空であること。
     */
    @Test
    public void testSearchNonExistObject() {

        // 事前準備
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        String objectKey1 = "temp/searchTestFile_" + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey1, uploadFile1);

        String pattern = "temp/dummy*";

        // @formatter:off
        String[] searchResult = given().get(applicationContextUrl
                + "api?pattern=" + pattern).as(String[].class);
        // @formatter:on

        assertThat(searchResult, is(emptyArray()));
    }

    @PostConstruct
    private void createS3Client() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }
}
