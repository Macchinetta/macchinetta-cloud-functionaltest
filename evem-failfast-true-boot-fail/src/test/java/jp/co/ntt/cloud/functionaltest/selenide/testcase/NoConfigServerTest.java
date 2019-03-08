/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class NoConfigServerTest extends TestCase {

    /**
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /**
     * テストデータ保存先
     */
    @Value("${path.report}")
    private String reportPath;

    /*
     * geckoドライバーバージョン
     */
    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    /**
     * テスト名取得
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * ユーザID
     */
    private String userId;

    /**
     * パスワード
     */
    private String password;

    @Override
    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            FirefoxDriverManager.getInstance().version(geckodriverVersion)
                    .setup();
        }

        // ブラウザの設定
        Configuration.browser = WebDriverRunner.MARIONETTE;

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @Override
    @After
    public void tearDown() {
        // ログイン状態の場合ログアウトする。
        HelloPage helloPage = open(applicationContextUrl, HelloPage.class);
        if (helloPage.isLoggedIn()) {
            helloPage.logout();
        }
    }

    /**
     * EVEM0302001<br>
     * spring.cloud.config.enabledの設定がfalseの時、クライアントAPがConfig Server無しで起動できること。
     */
    @Test
    public void getS3PropertiesFromLocalPropertiiesTest() {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        HelloPage helloWorldPage = open(applicationContextUrl, TopPage.class)
                .login(userId, password);

        $("h1").shouldHave(text("Hello world!"));
        $$("p").get(1).shouldHave(text("Taro Denden"));

        // @ConfigurationPropertiesで取得した値
        SelenideElement s3ConfigConfigurationPropertiesTable = helloWorldPage
                .getS3ConfigConfigurationPropertiesTable();
        // @Valueで取得した値
        SelenideElement s3ConfigValueTable = helloWorldPage
                .getS3ConfigValueTable();

        s3ConfigConfigurationPropertiesTable.shouldHave(text(
                "functionaltest.external.properties.config.repo"), text("tmp/"),
                text("save/"));
        s3ConfigValueTable.shouldHave(text(
                "functionaltest.external.properties.config.repo"), text("tmp/"),
                text("save/from/local/properties"));

        // 証跡取得
        screenshot(testName.getMethodName());
    }

}
