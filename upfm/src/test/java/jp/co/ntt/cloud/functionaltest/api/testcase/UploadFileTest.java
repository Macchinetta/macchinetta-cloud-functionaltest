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
package jp.co.ntt.cloud.functionaltest.api.testcase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
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

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class UploadFileTest {

    /**
     * 検索試験用ファイルパス
     */
    private static final String TEMP_SEARCH_TEST_FILE = "temp/searchTestFile_";

    /**
     * 削除試験用ファイルパス
     */
    private static final String TEMP_DELETE_TEST_FILE = "temp/deleteTestFile_";

    /**
     * リクエストパラメータ：複数オブジェクトキー、複数条件
     */
    private static final String URL_OBJECTKEYS_AND = "&objectkeys=";

    /**
     * リクエストパラメータ：複数オブジェクトキー
     */
    private static final String URL_OBJECTKEYS = "?objectkeys=";

    /**
     * リクエストパラメータ：オブジェクトキー
     */
    private static final String URL_OBJECTKEY = "?objectkey=";

    /**
     * RESTサーブレットURLパス
     */
    private static final String API_VER_FILE = "api/v1/file";

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${s3.prefix}")
    private String prefix;

    private AmazonS3 s3Client;

    @After
    public void tearDown() {

        // s3バケット内のオブジェクト（tempディレクトリ配下）を削除する。 削除後はディレクトリも削除されるため、新たにtempディレクトリを追加する。
        ObjectListing list = s3Client.listObjects(bucketName, prefix + "temp/");
        List<KeyVersion> keys = new ArrayList<KeyVersion>();
        if (list.getObjectSummaries() != null) {
            for (S3ObjectSummary s : list.getObjectSummaries()) {
                keys.add(new KeyVersion(s.getKey()));
            }
            DeleteObjectsRequest request2 = new DeleteObjectsRequest(bucketName);
            request2.setKeys(keys);
            s3Client.deleteObjects(request2);
            s3Client.putObject(bucketName, prefix + "temp/", "");
        }
    }

    /**
     * UPFM0101 001 ファイルのアップロードが行える事を確認する。
     * @throws IOException
     */
    @Test
    public void testUpload() throws IOException {

        // 事前準備
        File uploadFile = new File("src/test/resources/files/Liberty.jpg");

        // テスト実行:ファイルをアップロードする
        String response = given().multiPart(WebPagePathConstants.FILE,
                uploadFile).when().post(applicationContextUrl + API_VER_FILE)
                .asString();

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

        // アサート:アップロードに使用したファイルと、実施後にS3から取得したファイルを比較し、同一であることを確認する。
        assertTrue(Arrays.equals(b1, b2));
    }

    /**
     * UPFM0102 001 単一ファイルの削除が行える事を確認する。
     */
    @Test
    public void testDelete() {

        // 事前準備:削除するためのファイル(単一)をアップロードしておく
        File uploadFile = new File("src/test/resources/files/Gleaners.jpg");
        String objectKey = prefix + TEMP_DELETE_TEST_FILE + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey, uploadFile);

        // サスペンド:アップロード確認
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey));

        // テスト実行:ファイルを削除する
        given().delete(applicationContextUrl + API_VER_FILE + URL_OBJECTKEY
                + objectKey);

        // アサート:削除したファイルが、S3上に存在しないことを、SDKを使用して確認する。
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey));
    }

    /**
     * UPFM0103 001 複数ファイルの削除が行える事を確認する。
     */
    @Test
    public void testMultiDelete() {

        // 事前準備:削除するためのファイル(複数)をアップロードしておく
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        File uploadFile2 = new File("src/test/resources/files/Gleaners.jpg");
        String objectKey1 = prefix + TEMP_DELETE_TEST_FILE + UUID.randomUUID();
        String objectKey2 = prefix + TEMP_DELETE_TEST_FILE + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey1, uploadFile1);
        s3Client.putObject(bucketName, objectKey2, uploadFile2);

        // サスペンド:アップロード確認
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey1));
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey2));

        // テスト実行:ファイルを削除する
        given().delete(applicationContextUrl + API_VER_FILE + URL_OBJECTKEYS
                + objectKey1 + URL_OBJECTKEYS_AND + objectKey2);

        // アサート:削除したファイルが、S3上に存在しないことを、SDKを使用して確認する。
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey2));
    }

    /**
     * UPFM0104 001 ファイル検索が行える事を確認する。
     */
    @Test
    public void testSearch() {

        // 事前準備:検索するためのファイル(複数)をアップロードしておく
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        File uploadFile2 = new File("src/test/resources/files/Gleaners.jpg");
        String objectKey1 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();
        String objectKey2 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey1, uploadFile1);
        s3Client.putObject(bucketName, objectKey2, uploadFile2);

        // サスペンド:アップロード確認
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey1));
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey2));

        // 検索パターン
        String pattern = prefix + "temp/searchTestFile*";

        // テスト実行:ファイルを検索する
        String[] searchResult = given().get(applicationContextUrl
                + "api/v1/file?pattern=" + pattern).as(String[].class);
        List<String> list = Arrays.asList(searchResult);
        System.out.println("test" + searchResult[0]);
        String[] expectedArray = { objectKey1, objectKey2 };

        // アサート:検索結果のオブジェクトキーと、事前にファイルを配置した際のオブジェクトキーを比較して、同一であることを確認する。
        assertThat(list, hasItems(expectedArray));
    }

    /**
     * UPFM0105 001 S3上に存在しないオブジェクトキーを指定して単一ファイルの削除を行う。
     */
    @Test
    public void testDeleteNonExistObject() {

        // 事前準備:削除対象のオブジェクトキー(単一)を作成しておく
        String objectKey1 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();

        // サスペンド:削除対象のオブジェクトキーが存在しないことを確認
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));

        // テスト実行:オブジェクトキーを指定して削除する
        int status = given().delete(applicationContextUrl + API_VER_FILE
                + URL_OBJECTKEY + objectKey1).getStatusCode();

        // アサート:例外が発生せず、そのままAPIがリターンすること。
        assertThat(status, is(200));
    }

    /**
     * UPFM0105 002 S3上に存在しないオブジェクトキーを指定して複数ファイルの削除を行う。
     */
    @Test
    public void testDeleteNonExistObjects() {

        // 事前準備:削除対象のオブジェクトキー(複数)を作成しておく
        String objectKey1 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();
        String objectKey2 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();

        // サスペンド:削除対象のオブジェクトキーが存在しないことを確認
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey2));

        // テスト実行:オブジェクトキーを指定して削除する
        int status = given().delete(applicationContextUrl + API_VER_FILE
                + URL_OBJECTKEYS + objectKey1 + URL_OBJECTKEYS_AND + objectKey2)
                .getStatusCode();

        // アサート:例外が発生せず、そのままAPIがリターンすること。
        assertThat(status, is(200));
    }

    /**
     * UPFM0105 003 S3上に存在・非存在が混在しているオブジェクトキーを指定して複数ファイルの削除を行う。
     */
    @Test
    public void testDeleteExistAndNonExistObjects() {

        // 事前準備:削除対象のオブジェクトキー(複数)を作成しておく
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        String objectKey1 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();
        String objectKey2 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();

        // 事前準備:objectKey1をアップロードしておく
        s3Client.putObject(bucketName, objectKey1, uploadFile1);

        // サスペンド:削除対象のオブジェクトキーについて存在・非存在が混在していることを確認
        assertTrue(s3Client.doesObjectExist(bucketName, objectKey1));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey2));

        // テスト実行:オブジェクトキーを指定して削除する
        int status = given().delete(applicationContextUrl + API_VER_FILE
                + URL_OBJECTKEYS + objectKey1 + URL_OBJECTKEYS_AND + objectKey2)
                .getStatusCode();

        // アサート:例外が発生せず、S3上に存在するオブジェクトが削除されていること。
        assertThat(status, is(200));
        assertFalse(s3Client.doesObjectExist(bucketName, objectKey1));
    }

    /**
     * UPFM0106 001 S3上に存在しないオブジェクトキーパターンを指定してファイル検索を行う。
     */
    @Test
    public void testSearchNonExistObject() {

        // 事前準備:パターン検索するためのファイルをアップロードしておく
        File uploadFile1 = new File("src/test/resources/files/Liberty.jpg");
        String objectKey1 = prefix + TEMP_SEARCH_TEST_FILE + UUID.randomUUID();
        s3Client.putObject(bucketName, objectKey1, uploadFile1);

        // 検索パターン
        String pattern = "temp/dummy*";

        // テスト実行:ファイルを検索する
        String[] searchResult = given().get(applicationContextUrl + API_VER_FILE
                + "?pattern=" + pattern).as(String[].class);

        // アサート:検索条件にマッチする配列が空であること。
        assertThat(searchResult, is(emptyArray()));
    }

    @PostConstruct
    private void createS3Client() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }
}
