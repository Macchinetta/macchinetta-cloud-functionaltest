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

import static com.codeborne.selenide.Condition.exactText;
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
import jp.co.ntt.cloud.functionaltest.selenide.page.CaapPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CaapTest {

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

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @After
    public void tearDown() {

        // ログアウト
        open(applicationContextUrl, HomePage.class).logout();
    }

    /**
     * CAAP0101 001 EC2上のAP起動確認<br>
     * CAAP0102 001 ElastiCacheAutoConfigureの無効化確認
     */
    @Test
    public void testInspect() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // 事前準備:ログイン
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login("0000000002", "aaaaa11111");

                // サスペンド:画面遷移確認
                homePage.getH().shouldHave(exactText("Hello world!"));

                // テスト実行:AmazonElastiCacheの生成を行う。
                CaapPage caapPage = homePage.inspect();

                // アサート:AutoConfigurationでBeanとして生成対象となるAmazonElastiCacheがクラスパスに存在すること。
                caapPage.getExistFQCNClasspath().shouldHave(exactText("true"));

                // アサート:ApplicationContext内にAmazonElastiCacheが登録されていないこと。
                caapPage.getExistInApplicationContext().shouldHave(exactText(
                        "false"));

                // 証跡取得
                screenshot("testInspect");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }
}
