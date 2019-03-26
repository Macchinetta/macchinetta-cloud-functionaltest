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

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class HelloWorldTest {

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

        // ブラウザの設定
        Configuration.browser = "jp.co.ntt.cloud.functionaltest.selenide.testcase.FirefoxWebDriverProvider";

        // 検証メソッドタイムアウトの設定
        Configuration.timeout = 130000;

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
     * PRCD0101 001 アクセス制限が設定されているCloudFront上のファイルにアクセスできないことを確認する
     */
    @Test
    public void testPRCD0101UPAY() {

        // テスト実行:無償会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000001", "aaaaa11111");

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:CloudFront上のファイルにアクセスできないこと
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(empty);
        homePage.getReject().shouldHave(exactText("access denied"));

        // 証跡取得
        screenshot("PRCD0101_001");

    }

    /**
     * PRCD0101 002 署名付きCookieの有効期限前で、アクセス制限が設定されているCloudFront上のファイルにアクセスできないことを確認する<br>
     * PRCD0101 003 署名付きCookieを利用して、アクセス制限が設定されているCloudFront上のファイルにアクセスできることを確認する<br>
     * PRCD0101 004 署名付きCookieの有効期限きれで、アクセス制限が設定されているCloudFront上のファイルにアクセスできないことを確認する<br>
     * PRCD0101 005 署名付きCookieを利用して、アクセス制限が設定されているCloudFront上のファイルに、ポリシーで指定したリソースの範囲外にあるファイルにアクセスできないことを確認する。
     */
    @Test
    public void testPRCD0101_PAID() {

        // テスト実行:有償会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000002", "aaaaa11111");

        // サスペンド:5秒待機
        homePage.getTimer().shouldHave(value("00:00:05"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:有効期限前のため、CloudFront上のファイルにアクセスできないこと
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(empty);

        // 証跡取得
        screenshot("PRCD0101_002");

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:CloudFront上のファイルにアクセスできること
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(exactText(homePage
                .getAppResult().getText()));
        homePage.getReject().shouldHave(exactText("access denied"));

        // 証跡取得
        screenshot("PRCD0101_003and005");

        // サスペンド:課金コンテンツが無効になる2分後まで待機
        homePage.getTimer().shouldHave(value("00:02:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:有効期限ぎれのため、CloudFront上のファイルにアクセスできないこと
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(empty);

        // 証跡取得
        screenshot("PRCD0101_004");

    }

    /**
     * PRCD0101 006 署名付Cookieがログアウトして正常に削除されたことを確認するために署名付Cookie発行可能ユーザでログインして、
     * ログアウト後に著名付Cookieが発行できないユーザで再度ログインして、ファイルにアクセスできないことを確認する。
     */
    @Test
    public void testPRCD0101LogoutAndLogin() {

        // テスト実行:有償会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000002", "aaaaa11111");

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // サスペンド:CloudFront上のファイルにアクセスできること
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(exactText(homePage
                .getAppResult().getText()));

        screenshot("PRCD0101_006_1");

        // ログアウト
        homePage.logout();

        screenshot("PRCD0101_006_2");

        // テスト実行:無償会員でログインする。
        homePage = open(applicationContextUrl + "login.jsp", LoginPage.class)
                .login("0000000001", "aaaaa11111");

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:署名付Cookieがログアウトで消されたことに因って、署名付Cookieを発行できないユーザで再度ログインした際に、
        // CloudFront上のファイルにアクセスできないことを確認する。
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(empty);

        // 証跡取得
        screenshot("PRCD0101_006_3");

    }

    /**
     * PRCD0101 007 署名付Cookie削除がログアウトしないで再ログインした場合でも正常に削除されたことを確認するために
     * 署名付Cookie発行可能ユーザでログインして、ログアウトしないまま、著名付Cookieが発行できないユーザで再度ログインして、ファイルにアクセスできないことを確認する。
     */
    @Test
    public void testPRCD0101NotLogoutAndLogin() {

        // テスト実行:有償会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000002", "aaaaa11111");

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // サスペンド:CloudFront上のファイルにアクセスできること
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(exactText(homePage
                .getAppResult().getText()));

        screenshot("PRCD0101_007_1");

        // テスト実行:無償会員でログインする。
        homePage = open(applicationContextUrl + "login", LoginPage.class).login(
                "0000000001", "aaaaa11111");

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:署名付Cookieがログアウトで消されたことに因って、署名付Cookieを発行できないユーザで再度ログインした際に、
        // CloudFront上のファイルにアクセスできないことを確認する。
        homePage.getAppResult().shouldNotHave(empty);

        // CloudFront画像用のコンテンツ値が空であること。
        homePage.getCloudFrontResult().shouldHave(empty);

        // 証跡取得
        screenshot("PRCD0101_007_2");

    }

    /**
     * PRCD0101 008 有償会員でも署名付きCookieが発行されないページにアクセスした際にCloudFront上のファイルにアクセスできるないことを確認する
     */
    @Test
    public void testPRCD0101_008() {

        // テスト実行:有償会員でログインする。
        HomePage homePage = open(applicationContextUrl, LoginPage.class).login(
                "0000000002", "aaaaa11111");

        // サスペンド:課金コンテンツが無効になる2分後まで待機
        homePage.getTimer().shouldHave(value("00:02:00"));

        // disableCookieクリック
        homePage.disableCookie();

        // サスペンド:課金コンテンツが有効になる1分後まで待機
        homePage.getTimer().shouldHave(value("00:01:00"));

        // 再取得クリック後、判定用のコンテンツ値表示
        homePage.reload().loadVerificationContent();

        // アサート:CloudFront上のファイルにアクセスできないこと
        homePage.getAppResult().shouldNotHave(empty);
        homePage.getCloudFrontResult().shouldHave(empty);
        homePage.getReject().shouldHave(exactText("access denied"));

        // 証跡取得
        screenshot("PRCD0101_008");

    }

}
