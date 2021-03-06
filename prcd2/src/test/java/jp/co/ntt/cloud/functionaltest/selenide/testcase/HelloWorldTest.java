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

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

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

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /**
     * PRCD20101 001 署名付きCookieを利用して、アクセス制限が設定されているCloudFront上のファイルにIP制限に因ってアクセスできないことを確認する
     */
    @Test
    public void testPRCD20101_001() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // テスト実行:有償会員でログインする。
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login("0000000002", "aaaaa11111");

                // 再取得クリック後、判定用のコンテンツ値表示
                homePage.reload().loadVerificationContent();

                // アサート:CloudFront上のファイルにアクセスできないこと
                homePage.getAppResult().shouldNotHave(empty);
                homePage.getCloudFrontResult().shouldHave(empty);

                // 証跡取得
                screenshot("PRCD20101_001");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }

}
