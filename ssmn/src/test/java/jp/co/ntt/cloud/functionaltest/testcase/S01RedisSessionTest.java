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
package jp.co.ntt.cloud.functionaltest.testcase;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.tools.ant.BuildException;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import jp.co.ntt.cloud.functionaltest.domain.model.Cart;
import jp.co.ntt.cloud.functionaltest.domain.model.CartItem;
import jp.co.ntt.cloud.functionaltest.infra.redis.RedisUtil;
import jp.co.ntt.cloud.functionaltest.selenide.page.redissession.HomePage;
import jp.co.ntt.cloud.functionaltest.selenide.page.redissession.IndexPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.redissession.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.redissession.OrderConfirmPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.redissession.ViewCartPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class S01RedisSessionTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${until.session.timeout.sec}")
    private Integer untilSessionTimeout;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Inject
    private RedisUtil redisUtil;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws IOException {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // RestAssured のベースURI設定
        RestAssured.baseURI = this.applicationContextUrl;
    }

    @After
    public void tearDown() throws InterruptedException {

        // ログイン状態の場合ログアウトする。
        HomePage homePage = open(applicationContextUrl, HomePage.class);
        if (homePage.isLoggedIn()) {
            homePage.logout();
        }
    }

    /**
     * SSMN0201 001 SessionがRedisに保存され、Session情報の新規作成、更新ができること
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws BuildException
     */
    @Test
    public void session01RedisTest() throws IOException, InterruptedException, BuildException, ClassNotFoundException {

        // 事前準備
        redisUtil.flushRedis();

        // 注文してカートに商品を入れたあと、 カート表示する。
        ViewCartPage viewCartPage = open(applicationContextUrl + "order/form",
                LoginPage.class).loginOrderForm("0000000002", "aaaaa11111")
                        .orderOddIdProduct().viewCart();

        // Redisに保存されているセッション情報を取得
        String sessionId = viewCartPage.getSessionId().getText();
        Cart cart = redisUtil.getFromRedis(sessionId);

        // アサート:Redisに保存されているSessionと画面に表示したSession情報が一致していること。
        int index = 0;
        for (CartItem cartItem : cart.getCartItems()) {
            viewCartPage.getCartItemName(index).shouldHave(exactText(cartItem
                    .getProduct().getName()));
            viewCartPage.getCartItemPrice(index).shouldHave(text(
                    moneyUnitToString(cartItem.getProduct().getPrice())));
            index++;
        }

        // 証跡取得
        screenshot("session01RedisTest-viewCart");

        // セッション情報を更新する(カートに表示されているアイテムの個数を変更)。
        viewCartPage.changeQuantity(0, 7).changeQuantity(1, 5);

        // 証跡取得
        screenshot("session01RedisTest-viewCart-changeQuantity");

        // 注文内容確認
        OrderConfirmPage orderConfirmPage = viewCartPage.orderConfirm();

        // Redisに保存されているセッション情報を取得
        cart = redisUtil.getFromRedis(sessionId);

        // アサート:Session情報を更新すると、Redisに保存されているセッションも更新されていること。
        index = 0;
        for (CartItem cartItem : cart.getCartItems()) {
            orderConfirmPage.getCartItemName(index).shouldHave(exactText(
                    cartItem.getProduct().getName()));
            orderConfirmPage.getCartItemPrice(index).shouldHave(text(
                    moneyUnitToString(cartItem.getProduct().getPrice())));
            orderConfirmPage.getCartItemQuantity(index).shouldHave(exactText(
                    String.valueOf(cartItem.getQuantity())));
            index++;
        }

        // 証跡取得
        screenshot(testName.getMethodName() + "-orderConfirm");
    }

    /**
     * SSMN0202 001 ログイン後に認証が必要なページにアクセスしてから、ログアウトし、再度認証が必要なページにアクセスし、ログイン画面に遷移すること。
     * @throws InterruptedException
     */
    @Test
    public void session02NoSessionTest() throws InterruptedException {

        // POST メソッド実行
        IndexPage sessionIndexPage = open(applicationContextUrl
                + "session/isAuthenticated", LoginPage.class)
                        .loginIsAuthenticated("0000000002", "aaaaa11111");

        // サスペンド:画面遷移確認
        sessionIndexPage.getH2().shouldHave(exactText(
                "Session Management Functional Test(Is Authenticated)"));

        // 証跡取得
        screenshot(testName.getMethodName() + "-whenPost");

        // ログアウト
        open(applicationContextUrl, HomePage.class).logout();

        // 認証が必要なページにアクセスする
        // アサート:ログアウト後にセッションが無効化されていること
        open(applicationContextUrl + "session/isAuthenticated", LoginPage.class)
                .getMessage().shouldHave(exactText(
                        "Login with Username and Password"));
    }

    /**
     * SSMN0203 001 セッションタイムアウト後に認証が必要なページでGETを行い、Invalid Sessionエラーになること
     * @throws InterruptedException
     */
    @Test
    public void session03_01TimeoutTest() throws InterruptedException {

        // ログイン
        IndexPage sessionIndexPage = open(applicationContextUrl
                + "session/isAuthenticated", LoginPage.class)
                        .loginIsAuthenticated("0000000002", "aaaaa11111");

        // サスペンド:セッションタイムアウトまで待つ
        Thread.sleep((long) untilSessionTimeout * 1000);

        // GET メソッド実行
        // アサート:/error/invalidSessionに遷移すること
        sessionIndexPage.clickGetButtonAfterTimeout().getErrorTitle()
                .shouldHave(exactText("Session Invalid Error"));

        // 証跡取得
        screenshot(testName.getMethodName() + "-whenGet");
    }

    /**
     * SSMN0203 002 セッションタイムアウト後に認証が必要なページでPOSTを行い、Invalid Sessionエラーになること
     * @throws InterruptedException
     */
    @Test
    public void session03_02TimeoutTest() throws InterruptedException {

        // ログイン
        IndexPage sessionIndexPage = open(applicationContextUrl
                + "session/isAuthenticated", LoginPage.class)
                        .loginIsAuthenticated("0000000002", "aaaaa11111");

        // サスペンド:セッションタイムアウトまで待つ
        Thread.sleep((long) untilSessionTimeout * 1000);

        // POST メソッド実行
        // アサート:/error/invalidSessionに遷移すること
        sessionIndexPage.clickPostButtonAfterTimeout().getErrorTitle()
                .shouldHave(exactText("Session Invalid Error"));

        // 証跡取得
        screenshot(testName.getMethodName() + "-whenGet");
    }

    /**
     * 数値を通貨形式(String)に変換する
     * @param input
     * @return
     */
    private String moneyUnitToString(Integer input) {
        Locale locale = new Locale("ja", "JP");
        NumberFormat curIns = NumberFormat.getCurrencyInstance(locale);
        return curIns.format(input).replace("￥", "");
    }

}
