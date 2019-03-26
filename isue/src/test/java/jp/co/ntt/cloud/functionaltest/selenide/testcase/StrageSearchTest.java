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
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.SearchPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml",
        "classpath:META-INF/spring/functionaltest-env.xml" })
@SpringBootTest
public class StrageSearchTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${app.downloadDir}")
    private String DOWNLOAD_DIR;

    @Value("${app.uploadDir}")
    private String UPLOAD_DIR;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Inject
    private DynamoDBMapper dbMapper;

    private static final Logger logger = LoggerFactory.getLogger(
            StrageSearchTest.class);

    private AmazonS3 s3;

    private boolean beforeClassFlg = true;

    @Before
    public void setUp() throws InterruptedException {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // 実行環境に応じた絶対パスに変換
        String prjDir = System.getProperty("user.dir");
        DOWNLOAD_DIR = prjDir + DOWNLOAD_DIR;
        UPLOAD_DIR = prjDir + UPLOAD_DIR;

        s3 = AmazonS3ClientBuilder.defaultClient();

        // 1度のみの初期化処理
        // (@BeforeClassを付与したstaticメソッドで定義すると必要なインスタンスがDIされないためここで実装)
        if (beforeClassFlg) {

            // ダウンロードフォルダをクリアする
            cleanDir(DOWNLOAD_DIR);

            // テストデータファイルをS3へアップロードする
            testDataFileUpload(UPLOAD_DIR);

            // DynamoDBへの反映を待つ
            Thread.sleep(5000);

            // 検索条件を試験できるようデータを補正する（アップロード日付を過去日に更新）
            FileMetaData updateData = dbMapper.load(FileMetaData.class,
                    "USER0001-FILE0001.txt");
            updateData.setUploadDate("2017-09-01");
            dbMapper.save(updateData, SaveBehavior.UPDATE.config());

            beforeClassFlg = false;
        }

        // ログイン
        open(applicationContextUrl, LoginPage.class).login("0000000002",
                "aaaaa11111").getH().shouldHave(exactText("DynamoDB Search"));
    }

    @After
    public void tearDown() {

        // ログイン状態の場合ログアウトする。
        SearchPage helloPage = open(applicationContextUrl, SearchPage.class);
        if (helloPage.isLoggedIn()) {
            helloPage.logout();
        }
    }

    /**
     * 【前処理】指定フォルダ内のファイルを削除する。
     * @param path 対象フォルダ
     */
    private void cleanDir(String path) {
        logger.debug("cleanDir[{}]", path);
        File tgtDir = new File(path);
        for (File tgt : tgtDir.listFiles()) {
            if (tgt.isDirectory()) {

                // フォルダの場合はフォルダ内のファイルをすべて削除した後，フォルダ自身を削除
                if (tgt.listFiles().length > 0) {
                    cleanDir(tgt.getPath());
                }
                tgt.delete();
            } else {
                if (!".gitkeep".equals(tgt.getName())) {
                    tgt.delete();
                }
            }
        }
    }

    /**
     * 【前処理】テストデータファイルをS3にアップロードする。 規定の階層に配置されたテストデータファイルを，リネームしてS3の指定バケットへアップロードする。 ・アップロード元：
     * UPLOAD_DIR/[バケット名]/[ユーザID]/[アップロード対象ファイル] ・アップロード先： S3/[バケット名]/[ユーザID]-[アップロード対象ファイル] 【例】
     * 元：UPLOAD_DIR/functionaltest.fileupload.a/USER0001/FILE0001.txt 先：functionaltest.fileupload.aバケット/USER0001-FILE0001.txt
     * @param path 指定フォルダ内
     */
    private void testDataFileUpload(String path) {
        File uploadDir = new File(path);

        // バケット名のフォルダ分繰り返す
        for (File bucketDir : uploadDir.listFiles()) {
            String bucketName = bucketDir.getName();

            // ユーザIDのフォルダ分繰り返す
            for (File userDir : bucketDir.listFiles()) {
                String userId = userDir.getName();

                // アップロード対象ファイル分繰り返す
                for (File uploadFile : userDir.listFiles()) {

                    // S3へアップロード
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(uploadFile.length());
                    try {
                        s3.putObject(new PutObjectRequest(bucketName, userId
                                + "-" + uploadFile
                                        .getName(), new FileInputStream(uploadFile), metadata));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * ISUE0101 001 インターネットストレージ(S3)にアップロードしたファイルを、プライマリキー（ハッシュキー：objectKey）を指定して検索できることを確認する。
     */
    @Test
    public void searchByPkTest() {
        String objectKey = "USER0001-FILE0001.txt";

        // テスト実行:オブジェクトキーを指定して検索する。
        SearchPage searchPage = open(applicationContextUrl, SearchPage.class)
                .searchByPk(objectKey);

        // アサート:該当レコード1件が検索結果として取得できること。
        searchPage.getRows().shouldHaveSize(2);

        // アサート:検索結果のobjectKeyを使用してS3からファイルダウンロードできること。
        Map<String, SelenideElement> recordMap = searchPage.toRecordMap(1);
        recordMap.get("objectKey").shouldHave(exactText(objectKey));

        // ファイルダウンロード
        fileDownload(recordMap.get("bucketName").getText(), recordMap.get(
                "objectKey").getText(), recordMap.get("uploadUser").getText(),
                recordMap.get("fileName").getText());

        // アサート:ダウンロードしたファイルの内容が事前にアップロードしたファイルの内容と一致すること。
        fileCompare(recordMap.get("bucketName").getText(), recordMap.get(
                "uploadUser").getText(), recordMap.get("fileName").getText());

        // 証跡取得
        screenshot("searchByPk");
    }

    /**
     * ISUE0101 002 インターネットストレージ(S3)にアップロードしたファイルを、セカンダリインデックス（パーティションキー：uploadUser＆ソートキー：uploadDate）を指定して検索できることを確認する。
     */
    @Test
    public void searchByIndex_uploadUser_uploadDateTest() {
        String uploadUser = "USER0001";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String uploadDate = sdf.format(new Date());

        // テスト実行:アップロードユーザとアップロード日を指定して検索する。
        SearchPage searchPage = open(applicationContextUrl, SearchPage.class)
                .searchByIndex_uploadUser_uploadDate(uploadUser, uploadDate);

        // アサート:検索条件に一致するレコードのみが検索結果として取得できること。
        searchPage.getRows().shouldHave(CollectionCondition.sizeGreaterThan(0));

        for (int i = 1; i < searchPage.getRows().size(); i++) {

            // アサート:検索結果のobjectKeyを使用してS3からファイルダウンロードできること。
            Map<String, SelenideElement> recordMap = searchPage.toRecordMap(i);
            recordMap.get("uploadUser").shouldHave(exactText(uploadUser));
            recordMap.get("uploadDate").shouldHave(exactText(uploadDate));

            // ファイルダウンロード
            fileDownload(recordMap.get("bucketName").getText(), recordMap.get(
                    "objectKey").getText(), recordMap.get("uploadUser")
                            .getText(), recordMap.get("fileName").getText());

            // アサート:ダウンロードしたファイルの内容が事前にアップロードしたファイルの内容と一致すること。
            fileCompare(recordMap.get("bucketName").getText(), recordMap.get(
                    "uploadUser").getText(), recordMap.get("fileName")
                            .getText());
        }

        // 証跡取得
        screenshot("searchByIndex_uploadUser_uploadDate");
    }

    /**
     * ISUE0101 003 インターネットストレージ(S3)にアップロードしたファイルを、セカンダリインデックスのパーティションキー（bucketName）を指定してソートキー（size）の降順に検索できることを確認する。
     */
    @Test
    public void searchByIndex_bucketNameTest() {
        String bucketName = "functionaltest.fileupload.a";

        // テスト実行:バケット名を指定して検索する。
        SearchPage searchPage = open(applicationContextUrl, SearchPage.class)
                .searchByIndex_bucketName(bucketName);

        // アサート:検索条件に一致するレコードのみが検索結果として取得できること。
        searchPage.getRows().shouldHave(CollectionCondition.sizeGreaterThan(0));

        int preSize = -1;
        for (int i = 1; i < searchPage.getRows().size(); i++) {

            // アサート:検索結果のobjectKeyを使用してS3からファイルダウンロードできること。
            Map<String, SelenideElement> recordMap = searchPage.toRecordMap(i);
            recordMap.get("bucketName").shouldHave(exactText(bucketName));

            // size（降順確認）
            int size = Integer.parseInt(recordMap.get("size").getText());
            if (preSize >= 0) {

                // アサート:検索結果がソートキー順に取得できること。
                assertTrue(preSize >= size);
            }
            preSize = size;

            // ファイルダウンロード
            fileDownload(recordMap.get("bucketName").getText(), recordMap.get(
                    "objectKey").getText(), recordMap.get("uploadUser")
                            .getText(), recordMap.get("fileName").getText());

            // アサート:ダウンロードしたファイルの内容が事前にアップロードしたファイルの内容と一致すること。
            fileCompare(recordMap.get("bucketName").getText(), recordMap.get(
                    "uploadUser").getText(), recordMap.get("fileName")
                            .getText());
        }

        // 証跡取得
        screenshot("searchByIndex_bucketName");
    }

    /**
     * ISUE0102 001 インターネットストレージ(S3)に同一オブジェクトキーのファイルをほぼ同時にアップロードした際に，エラーが発生することなく更新情報がDynamoDBに反映されることを確認する。
     * （同時アップロードにより，S3へのアップロード順とS3からのイベント通知順が一致しなくなる可能性があり，その場合は古い情報のイベント通知はDynamoDBへの反映がスキップされる想定。）
     * @throws InterruptedException
     */
    @Test
    public void continuouslyUploadTest() throws InterruptedException {

        // アップロード先バケット名
        String bucketName = "functionaltest.fileupload.a";

        // アップロードオブジェクトキー
        String objectKey = "continuouslyUploadTest";

        // ファイル内容
        String contents = "file data";

        // アップロード回数
        int uploadCnt = 5;

        // DynamoDBから更新前レコードを取得する
        FileMetaData preData = dbMapper.load(FileMetaData.class, objectKey);
        long preVersion = preData == null ? 0L : preData.getVersion();

        // アップロード回数分，複数スレッドから同時にS3へアップロードする
        ExecutorService exec = Executors.newFixedThreadPool(uploadCnt);
        for (int i = 0; i < uploadCnt; i++) {
            exec.execute(new Runnable() {
                public void run() {

                    // S3へアップロード
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(contents.length());
                    try (InputStream is = new ByteArrayInputStream(contents
                            .getBytes())) {
                        s3.putObject(
                                new PutObjectRequest(bucketName, objectKey, is, metadata));
                    } catch (IOException e) {
                        logger.error("S3へのアップロードに失敗しました。", e);
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // サスペンド:DynamoDBへの反映を待つ（アップロード数×2秒）
        Thread.sleep(2000 * (long) uploadCnt);

        // DynamoDBから最終更新レコードを取得する
        FileMetaData postData = dbMapper.load(FileMetaData.class, objectKey);

        // 更新前後のVersion値の差から，更新回数を算出する。
        logger.info("更新回数:{}", postData.getVersion() - preVersion);

        // 更新回数 = アップロード回数 - スキップ回数 であることをログから確認する。
        // ※スキップ回数はWARNレベルで「より新しいデータが登録済のため何もしない」の文言が出力されている件数を数える
    }

    /**
     * ISUE0102 002 正常 "インターネットストレージ(S3)に同一オブジェクトキーのファイルの登録，更新，削除を連続的に行った際に，エラーが発生することなく登録，更新，削除されることを確認する。
     * （同一オブジェクトキーによる更新を連続的に行うと，S3へのアップロード順とS3からのイベント通知順が一致しなくなる可能性があり，最後に削除されるという順序性が崩れる可能性があるため，各操作の間で一定時間スリープする。）"
     * @throws InterruptedException
     */
    @Test
    public void continuouslyUploadUpdateDeleteTest() throws InterruptedException {

        // アップロード先バケット名
        String bucketName = "functionaltest.fileupload.a";

        // アップロードオブジェクトキー
        String objectKey = "continuouslyUploadUpdateDeleteTest";

        // S3へアップロード（登録）
        String contents = "file data";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contents.length());
        try (InputStream is = new ByteArrayInputStream(contents.getBytes())) {
            s3.putObject(
                    new PutObjectRequest(bucketName, objectKey, is, metadata));
        } catch (IOException e) {
            logger.error("S3への登録に失敗しました。", e);
            throw new RuntimeException(e);
        }

        // サスペンド:DynamoDBへの反映を待つ
        Thread.sleep(5000);

        // S3へアップロード（更新）
        String contentsU = "file data update";
        ObjectMetadata metadataU = new ObjectMetadata();
        metadataU.setContentLength(contentsU.length());
        try (InputStream isU = new ByteArrayInputStream(contentsU.getBytes())) {
            s3.putObject(
                    new PutObjectRequest(bucketName, objectKey, isU, metadataU));
        } catch (IOException e) {
            logger.error("S3の更新に失敗しました。", e);
            throw new RuntimeException(e);
        }

        // サスペンド:DynamoDBへの反映を待つ
        Thread.sleep(5000);

        // S3から削除
        s3.deleteObject(bucketName, objectKey);

        // サスペンド:DynamoDBへの反映を待つ
        Thread.sleep(5000);

        // S3から削除されていることを確認する
        try {
            s3.getObject(bucketName, objectKey);
            fail();
        } catch (AmazonS3Exception e) {

            // サスペンド:削除確認
            assertEquals("NoSuchKey", e.getErrorCode());
        }

        // アサート:最終的にファイルが削除された状態であることを確認する。
        FileMetaData postData = dbMapper.load(FileMetaData.class, objectKey);
        assertNull(postData);
    }

    /**
     * S3からファイルをダウンロードして保存する。
     * @param bucketName ダウンロード対象のバケット名
     * @param objectKey ダウンロード対象のオブジェクトキー
     * @param uploadUser アップロードユーザ（ファイル命名用）
     * @param fileName ファイル名（ファイル命名用）
     */
    private void fileDownload(String bucketName, String objectKey,
            String uploadUser, String fileName) {

        // S3からファイルをダウンロード
        logger.debug("ファイルダウンロード : バケット[{}] オブジェクトキー[{}]", bucketName,
                objectKey);
        S3Object s3File = s3.getObject(
                new GetObjectRequest(bucketName, objectKey));

        // 保存先のファイルを準備
        StringBuilder sbDirName = new StringBuilder();
        sbDirName.append(DOWNLOAD_DIR);
        sbDirName.append("\\");
        sbDirName.append(bucketName);
        sbDirName.append("\\");
        sbDirName.append(uploadUser);
        File saveDir = new File(sbDirName.toString());
        File saveFile = new File(sbDirName.toString() + "\\" + fileName);
        try {
            saveDir.mkdirs();
            saveFile.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // ダウンロードファイルを保存
        try (FileOutputStream fos = new FileOutputStream(saveFile);
                S3ObjectInputStream s3i = s3File.getObjectContent();) {

            byte buf[] = new byte[256];
            int len;
            while ((len = s3i.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ダウンロードしたファイルとアップロードしたファイルの内容を比較する。
     * @param bucketName 対象バケット名
     * @param uploadUser アップロードユーザ（ファイル命名用）
     * @param fileName ファイル名（ファイル命名用）
     */
    private void fileCompare(String bucketName, String uploadUser,
            String fileName) {
        StringBuilder sbFileName = new StringBuilder();
        sbFileName.append(bucketName);
        sbFileName.append("\\");
        sbFileName.append(uploadUser);
        sbFileName.append("\\");
        sbFileName.append(fileName);

        // アップロードファイルの参照を生成
        File uploadFile = new File(UPLOAD_DIR + "\\" + sbFileName);

        // ダウンロードファイルの参照を生成
        File downloadFile = new File(DOWNLOAD_DIR + "\\" + sbFileName);

        byte[] uploadFileBytes = new byte[(int) uploadFile.length()];
        byte[] downloadFileBytes = new byte[(int) downloadFile.length()];
        try (FileInputStream fisUp = new FileInputStream(uploadFile);
                FileInputStream fisDown = new FileInputStream(downloadFile);) {

            // それぞれのファイルの内容のバイト配列を比較することで内容の一致を確認
            while (fisUp.read(uploadFileBytes) > 0) {
                while (fisDown.read(downloadFileBytes) > 0) {
                    assertArrayEquals(uploadFileBytes, downloadFileBytes);
                }
            }

        } catch (IOException e) {
            new RuntimeException(e);
        }
    }
}
