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

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.codeborne.selenide.Configuration;

import jp.co.ntt.cloud.functionaltest.selenide.page.PriorityQueuePage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import junit.framework.TestCase;

/**
 * 優先度キューのテストクラス。
 * @author NTT 電電太郎
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class PriorityQueueTest extends TestCase {

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

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @After
    public void tearDown() {
        // ログイン状態の場合ログアウトする。
        PriorityQueuePage helloPage = open(applicationContextUrl,
                PriorityQueuePage.class);
        if (helloPage.isLoggedIn()) {
            helloPage.logout();
        }
    }

    /**
     * ログインを実行しプレミアム会員でメッセージ送信を行い結果を取得して10秒以内に処理されることを確認する。
     */
    @Test
    public void highPriorityQueueTest() {

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";

        // テスト実行
        open(applicationContextUrl, TopPage.class).login(userId, password);

        // アサーション
        $$("p").get(1).shouldHave(text("Hanako Denden"));
        String val = $("#processtime").getValue();

        assertTrue("プレミアム会員のため、メッセージを遅延なく処理される。", Long.valueOf(val) < 10);

        // 証跡取得
        screenshot("highPriorityQueueTest");
    }

    /**
     * ログインを実行し通常会員でメッセージ送信を行い結果を取得して10秒後に処理されることを確認する。
     */
    @Test
    public void lowPriorityQueueTest() {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        open(applicationContextUrl, TopPage.class).login(userId, password);

        // アサーション
        $$("p").get(1).shouldHave(text("Taro Denden"));
        String val = $("#processtime").getValue();
        if (StringUtils.isEmpty(val)) {
            fail("規定の時間を越えたため処理時間が取得できませんでした。");
        } else {
            assertTrue("通常会員のため、メッセージは10秒遅延処理される。", Long.valueOf(val) >= 10);
        }

        // 証跡取得
        screenshot("lowPriorityQueueTest");
    }

}
