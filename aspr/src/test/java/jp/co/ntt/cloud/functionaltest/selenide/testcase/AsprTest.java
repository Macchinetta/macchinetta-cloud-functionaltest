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
 */
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class AsprTest {

    private static final Logger logger = LoggerFactory.getLogger(AsprTest.class);

    /*
     * アプリケーション URL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * レポートパス
     */
    @Value("${path.report}")
    private String reportPath;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        logger.debug("+++ start setUp");
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // メッセージID管理テーブルのクリア
        jdbcTemplate.execute("TRUNCATE TABLE MESSAGE_ID_STORE");
        logger.debug("+++ end setUp");
    }

    /**
     * Selenide関連のクリア処理。
     * setUp()内で実施すると画面が真っ白になるなどの不具合が確認できたため、
     * 本メソッドで共通的に実施
     *
     * @throws Exception 予期しない例外
     */
    private void clearSetup() throws Exception {
        logger.debug("+++ start clearSetup");
        // Amazon SQS の指定キュー内メッセージを全削除
        open(applicationContextUrl + "deleteAll/reservation-queue");
        $(byId("status")).shouldHave(text("status:all-cleared"));

        open(applicationContextUrl + "deleteAll/reservation-deadletter");
        $(byId("status")).shouldHave(text("status:all-cleared"));

        // JMS リスナを停止
        open(applicationContextUrl + "stopListener");
        $(byId("status")).shouldHave(text("status:stopped"));

        // Webアプリケーションで内部管理されている受信メッセージ用BlockingQueueクリア
        open(applicationContextUrl + "clear");
        $(byId("status")).shouldHave(text("status:cleared"));
        logger.debug("+++ end clearSetup");
    }

    /**
     * 同期送信の確認。
     * SQSのreservation-queueにUUIDを送信することで、同一メッセージが同期受信できること。
     *
     * @throws Exception 意図しない例外
     */
    @Test
    public void testSendMessageBySync() throws Exception {
        logger.debug("+++ start testSendMessageBySync");

        clearSetup();

        // 事前準備：UUIDを1つ取得
        final String uuid = UUID.randomUUID().toString();

        // テスト実行
        open(applicationContextUrl + "send/" + uuid);

        // 検証：同期受信で1件受信し、キュー内のメッセージが送信時と同一であることを確認する。
        open(applicationContextUrl + "receiveSync");
        $(byId("status")).shouldHave(text("status:" + uuid));

        // 証跡取得
        screenshot("testSendMessageBySync");

        logger.debug("+++ end testSendMessageBySync");
    }

    /**
     * JMSリスナを使用し、3件のメッセージを非同期に受信する。
     * メッセージの受信件数を揃えるため、3件のメッセージ同期受信の後、JMSリスナを起動する。
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testReceiveMessageByAsync() throws Exception {
        logger.debug("+++ start testReceiveMessageByAsync");

        clearSetup();

        // 事前準備:3つのUUIDを取得し、同期送信を行う。
        String[] uuids = new String[3];
        for (int i = 0; i < uuids.length; i++) {
            uuids[i] = UUID.randomUUID().toString();
            open(applicationContextUrl + "send/" + uuids[i]);
            $(byId("status")).shouldHave(text("send:" + uuids[i]));
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

        // テスト実行: JMSリスナを起動する。
        open(applicationContextUrl + "startListener");
        $(byId("status")).shouldHave(text("status:started"));

        // 証跡取得
        screenshot("testReceiveMessageByAsync_startListener");

        // 検証: 非同期に受信したメッセージの取得
        open(applicationContextUrl + "receive/3");
        $(byId("status")).shouldHave(text("status:" + sortedUUIDs));

        // 証跡取得
        screenshot("testReceiveMessageByAsync_receive_async");

        // 検証：RDSのメッセージIDが3件存在すること(MESSAGE IDのみ記録されているため、件数のみ確認)。
        assertThat(
                jdbcTemplate.queryForList("SELECT MESSAGE_ID FROM MESSAGE_ID_STORE").size(),
                is(3));
        logger.debug("+++ end testReceiveMessageByAsync");
    }

    /**
     * JMSリスナによる非同期受信で重複したメッセージを受信した場合の挙動を確認する。
     * RDSによるメッセージIDの管理テーブルが一意制約違反を検出し、
     * 業務処理（ここではBlockingQueueにメッセージを追加）が行われず、
     * SQSスタンダードキューからのメッセージが削除されていることを確認する。
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testPseudoDuplicationReceive() throws Exception {
        logger.debug("+++ start testPseudoDuplicationReceive");

        clearSetup();

        // 事前準備:同期メッセージを1件送信
        final String uuid = UUID.randomUUID().toString();
        open(applicationContextUrl + "send/" + uuid);
        $(byId("status")).shouldHave(text("send:" + uuid));

        // 送信直後にAmazon SQS APIによるキュー参照を行うと空となることがあるため、
        // 可視性タイムアウトまで待つ。
        TimeUnit.SECONDS.sleep(30L);

        // 事前準備:Amazon SQS Java APIを使用し、SQSのキューに存在するメッセージIDを取得する。(メッセージを削除しない)
        open(applicationContextUrl + "browse/reservation-queue?delete=false");
        String messageIdView = $(byId("status")).getText();
        assertThat(messageIdView, is(not("status:EMPTY")));
        String messageId = "ID:" + messageIdView.substring("status:".length());

        // 事前準備:メッセージID管理テーブルに対し、二重受信エラーを発生させるため、
        // メッセージIDを先行登録する。
        int insertCount = jdbcTemplate.update("INSERT INTO MESSAGE_ID_STORE (MESSAGE_ID) VALUES (?)", messageId);
        assertThat(insertCount, is(1));

        // テスト実行(JMSリスナを起動)
        open(applicationContextUrl + "startListener");
        $(byId("status")).shouldHave(text("status:started"));

        // SQSデフォルト20秒のロングポーリングまで待つ。
        TimeUnit.SECONDS.sleep(20L);

        // 検証:業務処理による受信メッセージ件数が0であること。
        open(applicationContextUrl + "count");
        $(byId("status")).shouldHave(text("status:0"));

        // 証跡取得
        screenshot("testPseudoDuplicationReceive_receiveCount");

        // 検証:SQSのスタンダードキューからはJMSリスナにより受信が実施され、
        // メッセージは削除されていること。
        // すなわち、SQSのスタンダードキュー、デッドレターキューのいずれも0件であること。
        open(applicationContextUrl + "browse/reservation-queue?delete=true");
        $(byId("status")).shouldHave(text("status:EMPTY"));

        // 証跡取得
        screenshot("testPseudoDuplicationReceive_reservation-queue_empty");

        open(applicationContextUrl + "browse/reservation-deadletter?delete=true");
        $(byId("status")).shouldHave(text("status:EMPTY"));

        // 証跡取得
        screenshot("testPseudoDuplicationReceive_reservation-deadletter_empty");
        logger.debug("+++ end testPseudoDuplicationReceive");
    }

    @Test
    public void testDispatchDeadLetterQueue() throws Exception {
        logger.debug("+++ start testDispatchDeadLetterQueue");

        clearSetup();

        // 事前準備:同期メッセージを1件送信する。このとき、業務例外を受信時に発生させるため、
        // businessExceptionというキーワードを指定する。
        open(applicationContextUrl + "send/businessException");
        $(byId("status")).shouldHave(text("send:businessException"));

        // 送信直後にAmazon SQS APIによるキュー参照を行うと空となることがあるため、
        // 可視性タイムアウトまで待つ。
        TimeUnit.SECONDS.sleep(30L);

        // 事前準備:Amazon SQS Java APIを使用し、SQSのキューに存在するメッセージIDを取得する。(メッセージを削除しない)
        open(applicationContextUrl + "browse/reservation-queue?delete=false");
        String messageIdView = $(byId("status")).getText();
        assertThat(messageIdView, is(not("status:EMPTY")));
        String messageId = messageIdView.substring("status:".length());

        // テスト実行(JMSリスナを起動)
        open(applicationContextUrl + "startListener");
        $(byId("status")).shouldHave(text("status:started"));

        // SQSデフォルト20秒のロングポーリングまで待つ。
        TimeUnit.SECONDS.sleep(20L);

        // 検証:業務処理による受信メッセージ件数が0であること。
        open(applicationContextUrl + "count");
        $(byId("status")).shouldHave(text("status:0"));

        // 証跡取得
        screenshot("testDispatchDeadLetterQueue_receiveCount");

        // 検証:業務エラー起因による、最大10回の受信処理失敗により、SQSのスタンダードキュー内の
        // メッセージは削除され、デッドレターキューに移動していること。
        open(applicationContextUrl + "browse/reservation-queue?delete=true");
        $(byId("status")).shouldHave(text("status:EMPTY"));

        // 証跡取得
        screenshot("testDispatchDeadLetterQueue_reservation-queue_empty");

        // 検証:デッドレターキューに移動したメッセージIDが受信時と同一であること。
        open(applicationContextUrl + "browse/reservation-deadletter?delete=true");
        $(byId("status")).shouldHave(text("status:" + messageId));

        // 証跡取得
        screenshot("testPseudoDuplicationReceive_reservation-deadletter_exist");
        logger.debug("+++ end testDispatchDeadLetterQueue");
    }
}
