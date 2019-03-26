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

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class PriorityQueueTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // 検証メソッドタイムアウトの設定
        Configuration.timeout = 1200000;

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @After
    public void tearDown() {

        // ログイン状態の場合ログアウトする。
        HomePage homePage = open(applicationContextUrl, HomePage.class);
        if (homePage.isLoggedIn()) {
            homePage.logout();
        }
    }

    /**
     * PRQU0101 001 プレミアムユーザでログインすることで、優先度高のキューにメッセージ送信して、優先度高のキューのより受信を行い遅延なく処理されることを確認する。
     */
    @Test
    public void highPriorityQueueTest() {

        // テスト実行:プレミアム会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000001", "aaaaa11111");

        // サスペンド:画面遷移確認
        homePage.getAccountName().shouldHave(text("Hanako Denden"));

        // アサート:メッセージの処理時間は、優先度低の遅延キューに設定した10秒未満で終わることを確認する。
        String val = homePage.getProcesstime().getValue();
        assertTrue("プレミアム会員のため、メッセージを遅延なく処理される。", Long.valueOf(val) < 10);

        // 証跡取得
        screenshot("highPriorityQueueTest");
    }

    /**
     * PRQU0101 002 通常ユーザでログインすることで、優先度低のキューにメッセージ送信して、優先度高のキューのより受信を行い遅延なく処理されることを確認する。
     */
    @Test
    public void lowPriorityQueueTest() {

        // テスト実行:通常会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000002", "aaaaa11111");

        // サスペンド:画面遷移確認
        homePage.getAccountName().shouldHave(text("Taro Denden"));

        // アサート:メッセージの処理時間は、優先度低の遅延キューに設定した10秒以上で終わることを確認する。
        String val = homePage.getProcesstime().getValue();
        if (StringUtils.isEmpty(val)) {
            fail("規定の時間を越えたため処理時間が取得できませんでした。");
        } else {
            assertTrue("通常会員のため、メッセージは10秒遅延処理される。", Long.valueOf(val) >= 10);
        }

        // 証跡取得
        screenshot("lowPriorityQueueTest");
    }

}
