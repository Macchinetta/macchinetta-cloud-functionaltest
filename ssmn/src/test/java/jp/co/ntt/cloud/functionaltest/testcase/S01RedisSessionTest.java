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
package jp.co.ntt.cloud.functionaltest.testcase;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import com.codeborne.selenide.WebDriverRunner;

import io.restassured.RestAssured;
import jp.co.ntt.cloud.functionaltest.domain.model.Cart;
import jp.co.ntt.cloud.functionaltest.domain.model.CartItem;
import jp.co.ntt.cloud.functionaltest.infra.redis.RedisUtil;
import jp.co.ntt.cloud.functionaltest.selenide.page.AfterLoginOp;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.OrderConfirmPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.OrderFormPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.SessionIndexPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.SessionInvalidErrorPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.SessionSuccessGetPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.SessionSuccessPostPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.ViewCartPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/selenideContext.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class S01RedisSessionTest extends TestCase {

    @Rule
    public TestName testName = new TestName();

    /**
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /**
     * テスト結果出力先
     */
    @Value("${path.report}")
    private String reportPath;

    @Inject
    private RedisUtil redisUtil;

    /**
     * セッションタイムアウトするまでの時間(秒単位)
     */
    @Value("${until.session.timeout.sec}")
    private Integer untilSessionTimeout;

    /*
     * ユーザID
     */
    private String userId;

    /*
     * パスワード
     */
    private String password;

    @Override
    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
        // RestAssured のベースURI設定
        RestAssured.baseURI = this.applicationContextUrl;
    }

    @Override
    @After
    public void tearDown() {
        // ログイン状態の場合ログアウトする。
        AfterLoginOp afterLoginOp = open(applicationContextUrl, AfterLoginOp.class);
        if (afterLoginOp.isLoggedIn()) {
            afterLoginOp.logout();
        }
    }

    /**
     * SSMN0301001<br>
     * Redisにセッション情報が保存されていることを確認する。 セッション情報を更新すると、Redisに保存されているセッションも更新されていること。
     */
    @Test
    public void session01RedisTest() throws BuildException, ClassNotFoundException, IOException {

        // 事前準備
        redisUtil.flushRedis();
        userId = "0000000002";
        password = "aaaaa11111";

        open(applicationContextUrl + "order/form", LoginPage.class).login(userId, password);

        OrderFormPage orderFormPage = new OrderFormPage();

        // 注文してカートに商品を入れる
        orderFormPage.orderOddIdProduct();

        // カート表示
        ViewCartPage viewCartPage = orderFormPage.viewCart();

        // Redisに保存されているセッション情報を取得
        String sessionId = viewCartPage.getSessionId();
        Cart cart = redisUtil.getFromRedis(sessionId);

        // アサート
        int index = 0;
        for (CartItem cartItem : cart.getCartItems()) {
            assertEquals(viewCartPage.getCartItemName(index).getText(), cartItem.getProduct().getName());
            assertEquals(moneyUnitToInteger(viewCartPage.getCartItemPrice(index).getText()),
                    (Integer) cartItem.getProduct().getPrice());

            index++;
        }
        // 証跡取得
        screenshot("session01RedisTest-viewCart");

        // セッション情報を更新する(カートに表示されているアイテムの個数を変更)。
        viewCartPage.changeQuantity(0, 7);
        viewCartPage.changeQuantity(1, 5);
        // 証跡取得
        screenshot("session01RedisTest-viewCart-changeQuantity");


        // 注文内容確認
        OrderConfirmPage orderConfirmPage = viewCartPage.orderConfirm();
        // Redisに保存されているセッション情報を取得
        cart = redisUtil.getFromRedis(sessionId);
        // アサート
        index = 0;
        for (CartItem cartItem : cart.getCartItems()) {
            assertEquals(orderConfirmPage.getCartItemName(index).getText(), cartItem.getProduct().getName());
            assertEquals(moneyUnitToInteger(orderConfirmPage.getCartItemPrice(index).getText()),
                    (Integer) cartItem.getProduct().getPrice());
            assertEquals(Integer.parseInt(orderConfirmPage.getCartItemQuantity(index).getText()),
                    cartItem.getQuantity());
            index++;
        }
        // 証跡取得
        screenshot(testName.getMethodName() + "-orderConfirm");

        orderConfirmPage.orderFinish();
        screenshot(testName.getMethodName() + "-orderFinish");
    }

    /**
     * SSMN0201001,SSMN0201002<br>
     * 有効なセッションがある時に、正常画面へ遷移すること。
     */
    @Test
    public void session02GetAndPostTest() {
        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        open(applicationContextUrl + "session/isAuthenticated", LoginPage.class).login(userId, password);
        SessionIndexPage sessionIndexPage = new SessionIndexPage();

        // GET メソッド実行
        sessionIndexPage.getGetButton().click();
        SessionSuccessGetPage sessionSuccessGetPagePage = new SessionSuccessGetPage();
        assertEquals("Is Authenticated Get Success", sessionSuccessGetPagePage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenGet");

        open(applicationContextUrl + "session/isAuthenticated");

        // POST メソッド実行
        sessionIndexPage.getPostButton().click();
        SessionSuccessPostPage sessionSuccessPostPagePage = new SessionSuccessPostPage();
        assertEquals("Is Authenticated Post Success", sessionSuccessPostPagePage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenPost");
    }

    /**
     * SSMN0202001,SSMN0202002<br>
     * セッションタイムアウト時に、認証が必要なページでGET,POSTを行い、Session Invalidation Error 画面へ遷移すること。
     */
    @Test
    public void session03_01TimeoutTest() {
        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        open(applicationContextUrl + "session/isAuthenticated", LoginPage.class).login(userId, password);
        SessionIndexPage sessionIndexPage = new SessionIndexPage();

        // セッションタイムアウトまで待つ
        try {
            Thread.sleep(untilSessionTimeout * 1000);
        } catch (InterruptedException e) {
            fail();
        }

        // GET メソッド実行
        sessionIndexPage.getGetButton().click();
        SessionInvalidErrorPage sessionInvalidErrorPage = new SessionInvalidErrorPage();
        assertEquals("Session Invalid Error", sessionInvalidErrorPage.getErrorTitle().text());
        screenshot(testName.getMethodName() + "-whenGet");

        open(applicationContextUrl + "session/isAuthenticated", LoginPage.class).login(userId, password);

        // セッションタイムアウトまで待つ
        try {
            Thread.sleep(untilSessionTimeout * 1000);
        } catch (InterruptedException e) {
            fail();
        }

        // POST メソッド実行
        sessionIndexPage.getPostButton().click();
        assertEquals("Session Invalid Error", sessionInvalidErrorPage.getErrorTitle().text());
        screenshot(testName.getMethodName() + "-whenPost");
    }

    /**
     * SSMN0202003,SSMN0202004<br>
     * セッションタイムアウト時に、認証が必要ないページでGET,POSTを行い、正常画面へ遷移すること。
     */
    @Test
    public void session03_02TimeoutTest() {

        open(applicationContextUrl + "session/noAuthenticated");
        SessionIndexPage sessionIndexPage = new SessionIndexPage();

        // セッションタイムアウトまで待つ
        try {
            Thread.sleep(untilSessionTimeout * 1000);
        } catch (InterruptedException e) {
            fail();
        }

        // GET メソッド実行
        sessionIndexPage.getGetButton().click();
        SessionSuccessGetPage sessionSuccessGetPage = new SessionSuccessGetPage();
        assertEquals("No Authenticated Get Success", sessionSuccessGetPage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenGet");

        open(applicationContextUrl + "session/noAuthenticated");

        // セッションタイムアウトまで待つ
        try {
            Thread.sleep(untilSessionTimeout * 1000);
        } catch (InterruptedException e) {
            fail();
        }

        // POST メソッド実行
        sessionIndexPage.getPostButton().click();
        SessionSuccessPostPage sessionSuccessPostPage = new SessionSuccessPostPage();
        assertEquals("No Authenticated Post Success", sessionSuccessPostPage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenPost");
    }

    /**
     * SSMN0203001,SSMN0203002<br>
     * セッションがない時に、認証が必要なページでGET時にログイン画面に遷移し、POST時に、Session Invalidation Error
     * 画面へ遷移すること
     */
    @Test
    public void session04_01NoSessionTest() {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        open(applicationContextUrl + "session/isAuthenticated", LoginPage.class).login(userId, password);
        LoginPage loginPage = new LoginPage();

        SessionIndexPage sessionIndexPage = new SessionIndexPage();

        open(applicationContextUrl + "session/isAuthenticated");

        // セッション削除
        WebDriverRunner.clearBrowserCache();

        // GET メソッド実行
        sessionIndexPage.getGetButton().click();
        SessionInvalidErrorPage sessionInvalidErrorPage = new SessionInvalidErrorPage();
        assertEquals("Login with Username and Password", loginPage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenGet");

        loginPage.login(userId, password);

        open(applicationContextUrl + "session/isAuthenticated");

        // セッション削除
        WebDriverRunner.clearBrowserCache();

        // POST メソッド実行
        sessionIndexPage.getPostButton().click();
        assertEquals("Session Invalid Error", sessionInvalidErrorPage.getErrorTitle().text());
        screenshot(testName.getMethodName() + "-whenPost");
    }

    /**
     * SSMN0203003,SSMN0203004<br>
     * セッションがない時に、認証が必要ないページでGET、POST時に正常画面に遷移すること
     */
    @Test
    public void session04_02NoSessionTest() {

        open(applicationContextUrl + "session/noAuthenticated");
        SessionIndexPage sessionIndexPage = new SessionIndexPage();

        // セッション削除
        WebDriverRunner.clearBrowserCache();

        // GET メソッド実行
        sessionIndexPage.getGetButton().click();
        SessionSuccessGetPage sessionSuccessGetPagePage = new SessionSuccessGetPage();
        assertEquals("No Authenticated Get Success", sessionSuccessGetPagePage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenGet");

        open(applicationContextUrl + "session/noAuthenticated");

        // セッション削除
        WebDriverRunner.clearBrowserCache();

        // POST メソッド実行
        sessionIndexPage.getPostButton().click();
        SessionSuccessPostPage sessionSuccessPostPage = new SessionSuccessPostPage();
        assertEquals("No Authenticated Post Success", sessionSuccessPostPage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenPost");
    }

    /**
     * SSMN0204001<br>
     * ログイン後に認証が必要なページにアクセスしてから、ログアウトし、再度認証が必要なページにアクセスし、ログイン画面に遷移すること
     */
    @Test
    public void session05NoSessionTest() {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // ログイン
        open(applicationContextUrl + "session/isAuthenticated", LoginPage.class).login(userId, password);

        open(applicationContextUrl + "session/isAuthenticated");
        SessionIndexPage sessionIndexPage = new SessionIndexPage();

        // POST メソッド実行
        sessionIndexPage.getPostButton().click();
        SessionSuccessPostPage sessionSuccessPostPage = new SessionSuccessPostPage();
        assertEquals("Is Authenticated Post Success", sessionSuccessPostPage.getMessage().text());
        screenshot(testName.getMethodName() + "-whenPost");

        // ログアウト
        open(applicationContextUrl, AfterLoginOp.class).logout();

        // 認証が必要なページにアクセスする
        open(applicationContextUrl + "session/isAuthenticated");

        LoginPage loginPage = new LoginPage();
        // ログイン画面が表示されていること
        assertEquals("Login with Username and Password", loginPage.getMessage().text());
    }

    /**
     * SSMN0205001<br>
     * {@link jp.co.ntt.cloud.functionaltest.app.common.session.SessionEnforcerFilter}の適用除外設定をしたパスでヘルスチェックできること
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    @Test
    public void session06HealthCheck() throws ClientProtocolException, IOException {

        CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        // HealthCheckにアクセスしリダイレクトしていないことを確認する
        try (CloseableHttpResponse response = client
                .execute(new HttpGet(this.applicationContextUrl + "management/health"))) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        given().get("management/health").then().body("status", equalTo("UP"));
    }

    /**
     * 金額表現をintegerに変換する
     * @param input
     * @return
     */
    private  Integer moneyUnitToInteger(String input) {
        String result = "";
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isDigit(ch)) {
                result += ch;
            }
        }
        return Integer.parseInt(result);
    }

}
