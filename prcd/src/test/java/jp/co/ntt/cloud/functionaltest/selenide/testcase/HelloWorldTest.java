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

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class HelloWorldTest {

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
     * geckoドライバーバージョン
     */
    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

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

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            FirefoxDriverManager.getInstance().version(geckodriverVersion)
                    .setup();
        }

        // ブラウザの設定
        Configuration.browser = "jp.co.ntt.cloud.functionaltest.selenide.testcase.FirefoxWebDriverProvider";

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @After
    public void tearDown() {
        // ログイン状態の場合ログアウトする。
        HelloPage helloPage = open(applicationContextUrl, HelloPage.class);
        if (helloPage.isLoggedIn()) {
            helloPage.logout();
        }
    }

    /*
     * PRCD0101_001 無償会員でログインしてコンテンツが参照できないことを確認する。 またPre-Signed Cookieは発行していないが、リソースパス外のコンテンツも 参照できないこともあわせて確認する。
     */
    @Test
    public void testPRCD0101UPAY() throws InterruptedException {

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";

        // テスト実行
        TopPage topPage = open(applicationContextUrl, TopPage.class);

        HelloPage helloWorldPage = topPage.login(userId, password);

        // アサーション
        $("h1").shouldHave(text("Hello world!"));
        $$("p").get(1).shouldHave(text("Hanako Denden"));
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        String cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        $(byId("reject")).shouldHave(text("access denied"));

        // 証跡取得
        screenshot("PRCD0101_001");

    }

    /*
     * PRCD0101_002and003and004and005 有償会員でログインして有償コンテンツが参照できることを確認する。 その他に、Pre-Signed Cookieの有効期限きれにより、参照できなくなることと、
     * リソースパス外のコンテンツは参照できないこともあわせて確認する。
     */
    @Test
    public void testPRCD0101_PAID() throws InterruptedException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        TopPage topPage = open(applicationContextUrl, TopPage.class);

        HelloPage helloWorldPage = topPage.login(userId, password);

        // アサーション
        $("h1").shouldHave(text("Hello world!"));
        $$("p").get(1).shouldHave(text("Taro Denden"));
        Thread.sleep(5000);

        // コンテンツの閲覧期限になっていないので取得できない。
        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        String cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        // 証跡取得
        screenshot("PRCD0101_002");
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();

        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        cloudfrontVal = $(byId("cloudFrontResult")).val();
        String appVal = $(byId("appResult")).val();
        assertThat(cloudfrontVal, is(appVal));

        $(byId("reject")).shouldHave(text("access denied"));

        // 証跡取得
        screenshot("PRCD0101_003and005");
        Thread.sleep(60000);

        // コンテンツの閲覧期限を過ぎたので閲覧不可
        helloWorldPage.reload();
        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        // 証跡取得
        screenshot("PRCD0101_004");

    }

    /*
     * PRCD0101_006 有償会員でログインしてログアウト後に無償会員でログインした場合にクッキーが消えて コンテンツが参照できないことを確認する。
     */
    @Test
    public void testPRCD0101LogoutAndLogin() throws InterruptedException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        TopPage topPage = open(applicationContextUrl, TopPage.class);

        HelloPage helloWorldPage = topPage.login(userId, password);
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        String cloudfrontVal = $(byId("cloudFrontResult")).val();
        String appVal = $(byId("appResult")).val();
        assertThat(cloudfrontVal, is(appVal));

        screenshot("PRCD0101_006_1");

        helloWorldPage.logout();

        screenshot("PRCD0101_006_2");

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";
        topPage = open(applicationContextUrl + "login.jsp", TopPage.class);
        Thread.sleep(5000);

        helloWorldPage = topPage.login(userId, password);
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();

        cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        // 証跡取得
        screenshot("PRCD0101_006_3");

    }

    /*
     * PRCD0101_007 有償会員でログインしてログアウトせずに無償会員でログインした場合にクッキーが消えて コンテンツが参照できないことを確認する。
     */
    @Test
    public void testPRCD0101NotLogoutAndLogin() throws InterruptedException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        TopPage topPage = open(applicationContextUrl, TopPage.class);

        HelloPage helloWorldPage = topPage.login(userId, password);
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        String cloudfrontVal = $(byId("cloudFrontResult")).val();
        String appVal = $(byId("appResult")).val();
        assertThat(cloudfrontVal, is(appVal));

        screenshot("PRCD0101_007_1");

        // 事前準備
        userId = "0000000001";
        password = "aaaaa11111";
        topPage = open(applicationContextUrl + "login", TopPage.class);
        Thread.sleep(5000);

        helloWorldPage = topPage.login(userId, password);
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();

        cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        // 証跡取得
        screenshot("PRCD0101_007_2");

    }

    /*
     * PRCD0101_008 有料会員でログインしてCookieを発行しないコントローラにアクセスしてコンテンツが参照できないことを確認する。
     */
    @Test
    public void testPRCD0101_008() throws InterruptedException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        TopPage topPage = open(applicationContextUrl, TopPage.class);

        HelloPage helloWorldPage = topPage.login(userId, password);
        Thread.sleep(120000);

        // Cookie発行なし
        helloWorldPage.disableCookie();
        Thread.sleep(60000);

        // コンテンツの閲覧期限に達したので閲覧可能
        helloWorldPage.reload();
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();
        Thread.sleep(10000);

        String cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        $(byId("reject")).shouldHave(text("access denied"));

        // 証跡取得
        screenshot("PRCD0101_008");

    }

}
