/*
 * Copyright 2014-2017 NTT Corporation.
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

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;
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
import java.util.HashMap;
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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;
import jp.co.ntt.cloud.functionaltest.selenide.page.SearchPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import junit.framework.TestCase;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:META-INF/spring/selenideContext.xml",
		"classpath:META-INF/spring/functionaltest-env.xml" })
@SpringBootTest
public class StrageSearchTest extends TestCase {

    /** ロガー。 */
    private static final Logger logger = LoggerFactory
            .getLogger(StrageSearchTest.class);

	/** アプリケーションURL */
	@Value("${target.applicationContextUrl}")
	private String applicationContextUrl;

	/** アプリケーションURL */
	@Value("${path.report}")
	private String reportPath;

	/** ダウンロードファイル配置先 */
	@Value("${app.downloadDir}")
	private String DOWNLOAD_DIR;

	/** アップロードファイル配置先 */
	@Value("${app.uploadDir}")
	private String UPLOAD_DIR;

	/** AWS S3 アクセサ */
//	@Inject
	private AmazonS3 s3;

	/** AWS DynamoDBMapper */
	@Inject
	private DynamoDBMapper dbMapper;

	/** BeforeClassフラグ */
	private static boolean beforeClassFlg = true;


	/**
	 * 【前処理】メソッド単位の初期化処理。
	 */
	@Before
	public void setUp() {
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
			try {
				Thread.sleep(5000);
			} catch (Exception e) {}

			// 検索条件を試験できるようデータを補正する（アップロード日付を過去日に更新）
			FileMetaData updateData = 
					dbMapper.load(FileMetaData.class, "USER0001-FILE0001.txt");
			updateData.setUploadDate("2017-09-01");
			dbMapper.save(updateData, SaveBehavior.UPDATE.config());

			beforeClassFlg = false;
		}

		// ログイン

		// 事前準備
		String loginUserId = "0000000002";
		String loginPassword = "aaaaa11111";

		SearchPage uploadPage = open(applicationContextUrl, TopPage.class).login(loginUserId, loginPassword);
	}

	/**
	 * 【後処理】メソッド単位の終了処理。
	 */
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
	 * 
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
	 * 【前処理】テストデータファイルをS3にアップロードする。
	 * 規定の階層に配置されたテストデータファイルを，リネームしてS3の指定バケットへアップロードする。
	 *  ・アップロード元： UPLOAD_DIR/[バケット名]/[ユーザID]/[アップロード対象ファイル]
	 *  ・アップロード先： S3/[バケット名]/[ユーザID]-[アップロード対象ファイル]
	 *  【例】
	 *    元：UPLOAD_DIR/functionaltest.fileupload.a/USER0001/FILE0001.txt
	 *    先：functionaltest.fileupload.aバケット/USER0001-FILE0001.txt
	 * 
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
						s3.putObject(new PutObjectRequest(bucketName, userId + "-" + uploadFile.getName(),
								new FileInputStream(uploadFile), metadata));
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	/**
	 * <pre>
	 * 中項目ID: STRG0101
	 * CaseID: 001
	 * 試験項目：
	 *     インターネットストレージ(S3)にアップロードしたファイルを，
	 *     プライマリキー(ハッシュキー:objectKey)を指定して検索できることを確認する。
	 * 確認内容：
	 *     ・該当レコード1件が検索結果として取得できること。
	 *     ・検索結果のobjectKeyを使用してS3からファイルダウンロードできること。
	 *     ・ダウンロードしたファイルの内容が事前にアップロードしたファイルの内容と一致すること。
	 * </pre>
	 */
	@Test
	public void searchByPkTest() {
		String objectKey = "USER0001-FILE0001.txt";

		// テスト実行
		SearchPage searchPage = open(applicationContextUrl, SearchPage.class).searchByPk(objectKey);

		// 検索結果（table）取得
		ElementsCollection resultTable = $(byId("result")).$$("tr");

		// アサーション

		// 検索結果がヘッダ＋1件の全2行であること
		assertEquals(2, resultTable.size());

		// 該当レコードの取得項目値が条件指定値と一致すること
		SelenideElement resultRecord = resultTable.get(1);
		ElementsCollection resultColumns = resultRecord.$$("td");
		Map<String, String> recordMap = toRecordMap(resultColumns);
		// objectKey
		assertEquals(objectKey, recordMap.get("objectKey"));

		// ファイルダウンロード
		fileDownload(recordMap.get("bucketName"), recordMap.get("objectKey"), recordMap.get("uploadUser"),
				recordMap.get("fileName"));

		// ダウンロードファイルがアップロードしたファイルと一致することを比較
		fileCompare(recordMap.get("bucketName"), recordMap.get("uploadUser"), recordMap.get("fileName"));

		// 証跡取得
		screenshot("searchByPk");
	}

	/**
	 * <pre>
	 * 中項目ID: STRG0101
	 * CaseID: 002
	 * 試験項目：
	 *     インターネットストレージ(S3)にアップロードしたファイルを，
	 *     セカンダリインデックス(パーティションキー:uploadUser＆ソートキー:uploadDate)を指定して検索できることを確認する。
	 * 確認内容：
	 *     ・検索条件に一致するレコードのみが検索結果として取得できること。
	 *     ・検索結果のobjectKeyを使用してS3からファイルダウンロードできること。
	 *     ・ダウンロードしたファイルの内容が事前にアップロードしたファイルの内容と一致すること。
	 * </pre>
	 */
	@Test
	public void searchByIndex_uploadUser_uploadDateTest() {
		String uploadUser = "USER0001";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String uploadDate = sdf.format(new Date());

		// テスト実行
		SearchPage searchPage = open(applicationContextUrl, SearchPage.class)
				.searchByIndex_uploadUser_uploadDate(uploadUser, uploadDate);

		// 検索結果（table）取得
		ElementsCollection resultTable = $(byId("result")).$$("tr");

		// アサーション

		// 検索結果がヘッダ＋1件以上であること
		assertTrue(resultTable.size() > 1);
		for (int i = 1; i < resultTable.size(); i++) {

			// 該当レコードの取得項目値が条件指定値と一致すること
			SelenideElement resultRecord = resultTable.get(i);
			ElementsCollection resultColumns = resultRecord.$$("td");
			Map<String, String> recordMap = toRecordMap(resultColumns);
			// uploadUser
			assertEquals(uploadUser, recordMap.get("uploadUser"));
			// uploadDate
			assertEquals(uploadDate, recordMap.get("uploadDate"));

			// ファイルダウンロード
			fileDownload(recordMap.get("bucketName"), recordMap.get("objectKey"), recordMap.get("uploadUser"),
					recordMap.get("fileName"));

			// ダウンロードファイルがアップロードしたファイルと一致することを比較
			fileCompare(recordMap.get("bucketName"), recordMap.get("uploadUser"), recordMap.get("fileName"));
		}

		// 証跡取得
		screenshot("searchByIndex_uploadUser_uploadDate");
	}

	/**
	 * <pre>
	 * 中項目ID: STRG0101
	 * CaseID: 003
	 * 試験項目：
	 *     インターネットストレージ(S3)にアップロードしたファイルを，
	 *     セカンダリインデックスのパーティションキー(bucketName)を指定してソートキー(size)の降順に検索できることを確認する。
	 * 確認内容：
	 *     ・検索条件に一致するレコードのみが検索結果として取得できること。
	 *     ・検索結果がソートキー順に取得できること。
	 *     ・検索結果のobjectKeyを使用してS3からファイルダウンロードできること。
	 *     ・ダウンロードしたファイルの内容が事前にアップロードしたファイルの内容と一致すること。
	 * </pre>
	 */
	@Test
	public void searchByIndex_bucketNameTest() {
		String bucketName = "functionaltest.fileupload.a";

		// テスト実行
		SearchPage searchPage = open(applicationContextUrl, SearchPage.class).searchByIndex_bucketName(bucketName);

		// 検索結果（table）取得
		ElementsCollection resultTable = $(byId("result")).$$("tr");

		// アサーション

		// 検索結果がヘッダ＋1件以上であること
		assertTrue(resultTable.size() > 1);
		int preSize = -1;
		for (int i = 1; i < resultTable.size(); i++) {

			// 該当レコードの取得項目値が条件指定値と一致すること
			SelenideElement resultRecord = resultTable.get(i);
			ElementsCollection resultColumns = resultRecord.$$("td");
			Map<String, String> recordMap = toRecordMap(resultColumns);
			// bucketName
			assertEquals(bucketName, recordMap.get("bucketName"));
			// size（降順確認）
			int size = Integer.parseInt(recordMap.get("size"));
			if (preSize >= 0) {
				assertTrue(preSize >= size);
			}
			preSize = size;

			// ファイルダウンロード
			fileDownload(recordMap.get("bucketName"), recordMap.get("objectKey"), recordMap.get("uploadUser"),
					recordMap.get("fileName"));

			// ダウンロードファイルがアップロードしたファイルと一致することを比較
			fileCompare(recordMap.get("bucketName"), recordMap.get("uploadUser"), recordMap.get("fileName"));
		}

		// 証跡取得
		screenshot("searchByIndex_bucketName");
	}

	/**
	 * <pre>
	 * 中項目ID: STRG0102
	 * CaseID: 001
	 * 試験項目：
	 *     インターネットストレージ(S3)に同一オブジェクトキーのファイルをほぼ同時にアップロードした際に，
	 *     エラーが発生することなく更新情報がDynamoDBに反映されることを確認する。
	 *     （同時アップロードにより，S3へのアップロード順とS3からのイベント通知順が一致しなくなる可能性があり，
	 *       その場合は古い情報のイベント通知はDynamoDBへの反映がスキップされる想定。）
	 * 確認内容：
	 *     ・ERRORログが出力されないこと
	 *     ・[更新回数 = アップロード回数 - スキップ回数] であることをログから確認する。
	 *       ※スキップ回数はWARNレベルで「より新しいデータが登録済のため何もしない」の文言が出力されている件数を数える。
	 * </pre>
	 */
	@Test
	public void continuouslyUploadTest() {
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
		long preVersion = preData == null? 0L: preData.getVersion();
		
		// アップロード回数分，複数スレッドから同時にS3へアップロードする
		ExecutorService exec = Executors.newFixedThreadPool(uploadCnt);
		for (int i=0; i<uploadCnt; i++) {
			exec.execute(new Runnable() {
				public void run() {
					// S3へアップロード
					ObjectMetadata metadata = new ObjectMetadata();
					metadata.setContentLength(contents.length());
					try (InputStream is = new ByteArrayInputStream(contents.getBytes())) {
						s3.putObject(new PutObjectRequest(bucketName, objectKey, is, metadata));
					} catch (IOException e) {
						logger.error("S3へのアップロードに失敗しました。", e);
						throw new RuntimeException(e);
					}
				}
			});
		}
		// DynamoDBへの反映を待つ（アップロード数×2秒）
		try {
			Thread.sleep(2000 * uploadCnt);
		} catch (Exception e) {}
		
		// DynamoDBから最終更新レコードを取得する
		FileMetaData postData = dbMapper.load(FileMetaData.class, objectKey);
		
		// 更新前後のVersion値の差から，更新回数を算出する。
		logger.info("更新回数:{}", postData.getVersion() - preVersion);
		
		// 更新回数 = アップロード回数 - スキップ回数 であることをログから確認する。
		// ※スキップ回数はWARNレベルで「より新しいデータが登録済のため何もしない」の文言が出力されている件数を数える
	}

	/**
	 * <pre>
	 * 中項目ID: STRG0102
	 * CaseID: 002
	 * 試験項目：
	 *     インターネットストレージ(S3)に同一オブジェクトキーのファイルの登録，更新，削除を連続的に行った際に，
	 *     エラーが発生することなく登録，更新，削除されることを確認する。
	 *     （同一オブジェクトキーによる更新を連続的に行うと，
	 *      S3へのアップロード順とS3からのイベント通知順が一致しなくなる可能性があり，
	 *      最後に削除されるという順序性が崩れる可能性があるため，各操作の間で一定時間スリープする。）
	 * 確認内容：
	 *     ・ERRORログが出力されないこと
	 *     ・最終的にファイルが削除された状態であることを確認する。
	 * </pre>
	 */
	@Test
	public void continuouslyUploadUpdateDeleteTest() {
		// アップロード先バケット名
		String bucketName = "functionaltest.fileupload.a";
		// アップロードオブジェクトキー
		String objectKey = "continuouslyUploadUpdateDeleteTest";
		
		// S3へアップロード（登録）
		String contents = "file data";
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(contents.length());
		try (InputStream is = new ByteArrayInputStream(contents.getBytes())) {
			s3.putObject(new PutObjectRequest(bucketName, objectKey, is, metadata));
		} catch (IOException e) {
			logger.error("S3への登録に失敗しました。", e);
			throw new RuntimeException(e);
		}
		
		// DynamoDBへの反映を待つ
		try {
			Thread.sleep(5000);
		} catch (Exception e) {}

		// S3へアップロード（更新）
		String contentsU = "file data update";
		ObjectMetadata metadataU = new ObjectMetadata();
		metadataU.setContentLength(contentsU.length());
		try (InputStream isU = new ByteArrayInputStream(contentsU.getBytes())) {
			s3.putObject(new PutObjectRequest(bucketName, objectKey, isU, metadataU));
		} catch (IOException e) {
			logger.error("S3の更新に失敗しました。", e);
			throw new RuntimeException(e);
		}
		
		// DynamoDBへの反映を待つ
		try {
			Thread.sleep(5000);
		} catch (Exception e) {}

		// S3から削除
		s3.deleteObject(bucketName, objectKey);
		
		// DynamoDBへの反映を待つ
		try {
			Thread.sleep(5000);
		} catch (Exception e) {}

		// S3から削除されていることを確認する
		try {
			S3Object postFile = s3.getObject(bucketName, objectKey);
			fail();
		} catch (AmazonS3Exception e) {
			assertEquals("NoSuchKey", e.getErrorCode());
		}
		// DynamoDBから削除されていることを確認する
		FileMetaData postData = dbMapper.load(FileMetaData.class, objectKey);
		assertNull(postData);
	}

	/**
	 * 検索結果TABLE.TRタグ内のTDデータをMapに変換する。
	 * 
	 * @param elements TABLE.TRタグのコンテンツ（1レコード分のデータ）
	 * @return map 変換後Map
	 */
	private Map<String, String> toRecordMap(ElementsCollection elements) {
		Map<String, String> map = new HashMap<>();
		map.put("objectKey", elements.get(0).getText());
		map.put("bucketName", elements.get(1).getText());
		map.put("fileName", elements.get(2).getText());
		map.put("size", elements.get(3).getText());
		map.put("uploadUser", elements.get(4).getText());
		map.put("uploadDate", elements.get(5).getText());
		map.put("sequencer", elements.get(6).getText());
		map.put("version", elements.get(7).getText());
		return map;
	}

	/**
	 * S3からファイルをダウンロードして保存する。
	 * 
	 * @param bucketName ダウンロード対象のバケット名
	 * @param objectKey ダウンロード対象のオブジェクトキー
	 * @param uploadUser アップロードユーザ（ファイル命名用）
	 * @param fileName ファイル名（ファイル命名用）
	 */
	private void fileDownload(String bucketName, String objectKey, String uploadUser, String fileName) {
		// S3からファイルをダウンロード
		logger.debug("ファイルダウンロード : バケット[{}] オブジェクトキー[{}]", bucketName, objectKey);
		S3Object s3File = s3.getObject(new GetObjectRequest(bucketName, objectKey));
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
	 * 
	 * @param bucketName 対象バケット名
	 * @param uploadUser アップロードユーザ（ファイル命名用）
	 * @param fileName ファイル名（ファイル命名用）
	 */
	private void fileCompare(String bucketName, String uploadUser, String fileName) {
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
			fisUp.read(uploadFileBytes);
			fisDown.read(downloadFileBytes);
			assertArrayEquals(uploadFileBytes, downloadFileBytes);

		} catch (IOException e) {
			new RuntimeException(e);
		}
	}
}
