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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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
public class ConfigServerS3Test {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /**
     * EVEM0102 001 S3 から環境依存値を取得し、画面に表示できること
     */
    @Test
    public void getS3PropertiesFromConfigServerTest() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:ログインする。
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login("0000000002", "aaaaa11111");

                // アサート:@ConfigurationPropertiesで取得した値が表示されること。
                homePage.getS3ConfigConfigurationPropertiesTable().shouldHave(
                        text("functionaltest.external.properties.config.repo"),
                        text("tmp/"), text("save/"));

                // アサート:@Valueで取得した値が表示されること。
                homePage.getS3ConfigValueTable().shouldHave(text(
                        "functionaltest.external.properties.config.repo"), text(
                                "tmp/"), text("save/"));

                // 証跡取得
                screenshot(testName.getMethodName());
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

}
