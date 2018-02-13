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

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

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

import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/selenideContext.xml" })
public class ConfigServerS3Test extends TestCase {

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
     * EVEM0102001<br>
     * ログインを実行しConfig Serverから取得したプロパティが表示されることを確認する。
     */
    @Test
    public void getS3PropertiesFromConfigServerTest() {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        HelloPage helloWorldPage = open(applicationContextUrl, TopPage.class).login(userId, password);

        $("h1").shouldHave(text("Hello world!"));
        $$("p").get(1).shouldHave(text("Taro Denden"));

        // @ConfigurationPropertiesで取得した値
        SelenideElement s3ConfigConfigurationPropertiesTable = helloWorldPage.getS3ConfigConfigurationPropertiesTable();
        // @Valueで取得した値
        SelenideElement s3ConfigValueTable = helloWorldPage.getS3ConfigValueTable();

        s3ConfigConfigurationPropertiesTable.shouldHave(text("functionaltest.external.properties.config.repo"),
                text("tmp/"), text("save/"));
        s3ConfigValueTable.shouldHave(text("functionaltest.external.properties.config.repo"), text("tmp/"),
                text("save/"));

        // 証跡取得
        screenshot(testName.getMethodName());
    }

}
