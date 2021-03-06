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

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class AsprTest {

    /**
     * リクエストパラメータ：削除有効
     */
    private static final String DELETE_TRUE = "?delete=true";

    /**
     * リクエストパラメータ：削除無効
     */
    private static final String DELETE_FALSE = "?delete=false";

    /**
     * URLパスパラメータ：デッドレターキュー
     */
    private static final String FUNC_RESERVATION_DEADLETTER = "/func-reservation-deadletter-12x";

    /**
     * URLパスパラメータ：標準キュー
     */
    private static final String FUNC_RESERVATION_QUEUE = "/func-reservation-queue-12x";

    /**
     * View論理名：メッセージ全削除
     */
    public static final String DELETEALL = "deleteAll";

    /**
     * View論理名：メッセージ送信
     */
    public static final String SEND = "send";

    /**
     * View論理名：メッセージ返信
     */
    public static final String RECEIVE = "receive";

    /**
     * ロガー
     */
    private static final Logger logger = LoggerFactory.getLogger(
            AsprTest.class);

    /**
     * メッセージ用UUIDキャッシュ
     */
    private String uuidCache;

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        logger.debug("+++ start setUp");

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // 検証メソッドタイムアウトの設定
        Configuration.timeout = 1200000;

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // メッセージID管理テーブルのクリア
        jdbcTemplate.execute("TRUNCATE TABLE MESSAGE_ID_STORE");

        logger.debug("+++ end setUp");
    }

    /**
     * Selenide関連のクリア処理。 setUp()内で実施すると画面が真っ白になるなどの不具合が確認できたため、 本メソッドで共通的に実施
     */
    private void clearSetup() {
        logger.debug("+++ start clearSetup");

        // UUIDキャッシュ初期化
        uuidCache = null;

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // SQSスタンダードキュー内メッセージを全削除
                // サスペンド:削除確認
                open(applicationContextUrl + DELETEALL + FUNC_RESERVATION_QUEUE,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:all-cleared"));

                // デッドレターキュー内メッセージを全削除
                // サスペンド:削除確認
                open(applicationContextUrl + DELETEALL
                        + FUNC_RESERVATION_DEADLETTER, HomePage.class)
                                .getStatus().shouldHave(text(
                                        "status:all-cleared"));

                // JMS リスナを停止
                // サスペンド:停止確認
                open(applicationContextUrl + WebPagePathConstants.STOPLISTENER,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:stopped"));

                // Webアプリケーションで内部管理されている受信メッセージ用BlockingQueueクリア
                // サスペンド:削除確認
                open(applicationContextUrl + WebPagePathConstants.CLEAR,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:cleared"));
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
        logger.debug("+++ end clearSetup");
    }

    /**
     * ASPR0101 001 SQSスタンダードキューを使用した同期メッセージの送信を確認する。
     */
    @Test
    public void testSendMessageBySync() {
        logger.debug("+++ start testSendMessageBySync");

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                clearSetup();

                boolean appliesCache = false;
                boolean appliesSlash = false;

                // 事前準備:UUIDを1つ取得
                final String uuid = generateUuid(appliesCache, appliesSlash);

                appliesCache = true;
                appliesSlash = true;

                // テスト実行:UUIDを同期送信する。
                // サスペンド:送信確認
                open(applicationContextUrl + SEND + generateUuid(appliesCache,
                        appliesSlash), HomePage.class).getStatus().shouldHave(
                                text("send:" + uuid));

                // アサート:同期受信で1件受信し、キュー内のメッセージが送信時と同一であることを確認する。
                open(applicationContextUrl + WebPagePathConstants.RECEIVESYNC,
                        HomePage.class).getStatus().shouldHave(text("status:"
                                + uuid));

                // 証跡取得
                screenshot("testSendMessageBySync");

                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

        logger.debug("+++ end testSendMessageBySync");
    }

    /**
     * ASPR0201 001 SQSスタンダードキューに加え、Spring JMSの@JmsListenerを使用した非同期受信の動作を確認する。
     */
    @Test
    public void testReceiveMessageByAsync() {
        logger.debug("+++ start testReceiveMessageByAsync");

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                clearSetup();

                // 事前準備:3つのUUIDを取得し、同期送信を行う。
                String[] uuids = new String[3];
                for (int i = 0; i < uuids.length; i++) {
                    boolean appliesCache = false;
                    boolean appliesSlash = false;
                    uuids[i] = generateUuid(appliesCache, appliesSlash);

                    appliesCache = true;
                    appliesSlash = true;
                    // サスペンド:送信確認
                    open(applicationContextUrl + SEND + generateUuid(
                            appliesCache, appliesSlash), HomePage.class)
                                    .getStatus().shouldHave(text("send:"
                                            + uuids[i]));
                }

                // 辞書順にソートしたUUIDを控えておく。（スタンダードキューは順序性を担保しないため）
                Arrays.sort(uuids);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < uuids.length; i++) {
                    sb.append(uuids[i]);
                    if (i < uuids.length - 1) {
                        sb.append(",");
                    }
                }
                final String sortedUUIDs = sb.toString();

                // テスト実行:JMSリスナを起動する。
                // サスペンド:起動確認
                open(applicationContextUrl + WebPagePathConstants.STARTLISTENER,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:started"));

                // 証跡取得
                screenshot("testReceiveMessageByAsync_startListener");

                // アサート:非同期に受信したメッセージの取得
                open(applicationContextUrl + RECEIVE + "/3", HomePage.class)
                        .getStatus().shouldHave(text("status:" + sortedUUIDs));

                // 証跡取得
                screenshot("testReceiveMessageByAsync_receive_async");

                // アサート:RDSのメッセージIDが3件存在すること(MESSAGE IDのみ記録されているため、件数のみ確認)。
                assertThat(jdbcTemplate.queryForList(
                        "SELECT MESSAGE_ID FROM MESSAGE_ID_STORE").size(), is(
                                3));
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

        logger.debug("+++ end testReceiveMessageByAsync");
    }

    /**
     * ASPR0202 001 Spring JMSの@JmsListenerを利用したでSQSスタンダードキューの二重受信が発生した場合、後続のメッセージを受けて起動する業務処理が抑止できることを確認する。
     * @throws InterruptedException
     */
    @Test
    public void testPseudoDuplicationReceive() throws InterruptedException {
        logger.debug("+++ start testPseudoDuplicationReceive");

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                clearSetup();

                boolean appliesCache = false;
                boolean appliesSlash = false;

                // 事前準備:同期メッセージを1件送信
                // サスペンド:送信確認
                final String uuid = generateUuid(appliesCache, appliesSlash);

                appliesCache = true;
                appliesSlash = true;
                open(applicationContextUrl + SEND + generateUuid(appliesCache,
                        appliesSlash), HomePage.class).getStatus().shouldHave(
                                text("send:" + uuid));

                // サスペンド:送信直後にAmazon SQS APIによるキュー参照を行うと空となることがあるため可視性タイムアウトまで待つ。
                TimeUnit.SECONDS.sleep(30L);

                // 事前準備:Amazon SQS Java APIを使用し、SQSのキューに存在するメッセージIDを取得する。(メッセージを削除しない)
                HomePage page = open(applicationContextUrl + "browse"
                        + FUNC_RESERVATION_QUEUE + DELETE_FALSE,
                        HomePage.class);

                // サスペンド:取得確認
                page.getStatus().shouldNotHave(text("status:EMPTY"));

                // 事前準備:メッセージID管理テーブルに対し、二重受信エラーを発生させるためメッセージIDを先行登録する。
                String messageId = "ID:" + page.getStatus().getText().substring(
                        "status:".length());
                int insertCount = jdbcTemplate.update(
                        "INSERT INTO MESSAGE_ID_STORE (MESSAGE_ID) VALUES (?)",
                        messageId);

                // サスペンド:登録確認
                assertThat(insertCount, is(1));

                // テスト実行:JMSリスナを起動する。
                // サスペンド:起動確認
                open(applicationContextUrl + WebPagePathConstants.STARTLISTENER,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:started"));

                // サスペンド:SQSデフォルト20秒のロングポーリングまで待つ。
                TimeUnit.SECONDS.sleep(20L);

                // アサート:業務処理による受信メッセージ件数が0であること。
                open(applicationContextUrl + WebPagePathConstants.COUNT,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:0"));

                // 証跡取得
                screenshot("testPseudoDuplicationReceive_receiveCount");

                // アサート:SQSのスタンダードキューからメッセージは削除されていること。
                open(applicationContextUrl + "browse" + FUNC_RESERVATION_QUEUE
                        + DELETE_TRUE, HomePage.class).getStatus().shouldHave(
                                text("status:EMPTY"));

                // 証跡取得
                screenshot(
                        "testPseudoDuplicationReceive_func-reservation-queue-12x_empty");

                // アサート:デッドレターキューからメッセージは削除されていること。
                open(applicationContextUrl + "browse"
                        + FUNC_RESERVATION_DEADLETTER + DELETE_TRUE,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:EMPTY"));

                // 証跡取得
                screenshot(
                        "testPseudoDuplicationReceive_func-reservation-deadletter-12x_empty");

                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

        logger.debug("+++ end testPseudoDuplicationReceive");
    }

    /**
     * ASPR0203 001 非同期受信時の業務処理が失敗し続けた場合、SQSスタンダードキューから指定されたデッドレターキューに移動することを確認する。
     * @throws InterruptedException
     */
    @Test
    public void testDispatchDeadLetterQueue() throws InterruptedException {
        logger.debug("+++ start testDispatchDeadLetterQueue");

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                clearSetup();

                // 事前準備:同期メッセージを1件送信する。このとき、業務例外を受信時に発生させるため、businessExceptionというキーワードを指定する。
                // サスペンド:送信確認
                open(applicationContextUrl + SEND + "/businessException",
                        HomePage.class).getStatus().shouldHave(text(
                                "send:businessException"));

                // サスペンド:送信直後にAmazon SQS APIによるキュー参照を行うと空となることがあるため可視性タイムアウトまで待つ。
                TimeUnit.SECONDS.sleep(30L);

                // 事前準備:Amazon SQS Java APIを使用し、SQSのキューに存在するメッセージIDを取得する。(メッセージを削除しない)
                HomePage page = open(applicationContextUrl + "browse"
                        + FUNC_RESERVATION_QUEUE + DELETE_FALSE,
                        HomePage.class);

                // サスペンド:取得確認
                page.getStatus().shouldNotHave(text("status:EMPTY"));

                // 取得したメッセージIDを控える。
                String messageId = page.getStatus().getText().substring(
                        "status:".length());

                // テスト実行:JMSリスナを起動する。
                // サスペンド:起動確認
                open(applicationContextUrl + WebPagePathConstants.STARTLISTENER,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:started"));

                // サスペンド:SQSデフォルト20秒のロングポーリングまで待つ。
                TimeUnit.SECONDS.sleep(20L);

                // アサート:業務処理による受信メッセージ件数が0であること。
                open(applicationContextUrl + WebPagePathConstants.COUNT,
                        HomePage.class).getStatus().shouldHave(text(
                                "status:0"));

                // 証跡取得
                screenshot("testDispatchDeadLetterQueue_receiveCount");

                // アサート:業務エラー起因による、最大10回の受信処理失敗により、SQSのスタンダードキュー内のメッセージは削除され、デッドレターキューに移動していること。
                open(applicationContextUrl + "browse" + FUNC_RESERVATION_QUEUE
                        + DELETE_TRUE, HomePage.class).getStatus().shouldHave(
                                text("status:EMPTY"));

                // 証跡取得
                screenshot(
                        "testDispatchDeadLetterQueue_func-reservation-queue-12x_empty");

                // アサート:デッドレターキューに移動したメッセージIDが受信時と同一であること。
                open(applicationContextUrl + "browse"
                        + FUNC_RESERVATION_DEADLETTER + DELETE_TRUE,
                        HomePage.class).getStatus().shouldHave(text("status:"
                                + messageId));

                // 証跡取得
                screenshot(
                        "testPseudoDuplicationReceive_func-reservation-deadletter-12x_exist");

                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

        logger.debug("+++ end testDispatchDeadLetterQueue");
    }

    /**
     * UUID生成メソッド。キャッシュを利用するかを、URLパスパラメータ用に利用するかを指定する。
     * @param appliesCache キャッシュ利用フラグ
     * @param appliesSlash スラッシュ付与フラグ
     * @return
     */
    private String generateUuid(boolean appliesCache, boolean appliesSlash) {
        String uuid = null;

        if (appliesCache) {
            uuid = new String(uuidCache);
        } else {
            uuid = UUID.randomUUID().toString();
            uuidCache = new String(uuid);
        }

        if (appliesSlash) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(uuid);
            uuid = sb.toString();
        }

        return uuid;
    }
}
