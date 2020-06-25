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
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class AtscTest {

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

    /**
     * ATSC0101 001 アプリケーションのヒープ最大値送信をトリガとし、オートスケーリングの代用としてSNS通知によるシュミレートが可能であることを確認する。
     */
    @Test
    public void testStartListen() {
        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:スナーからアラーム通知イベントを取得する。
                HomePage homePage = open(applicationContextUrl, HomePage.class)
                        .clickListen();

                // アサート:画面上にしきい値超えによる通知内容を確認する。
                homePage.getNewState().shouldHave(exactText("ALARM"));
                homePage.getNewStateReason().shouldHave(text(
                        "Threshold Crossed"));
                homePage.getMetricName().shouldHave(exactText(
                        "HeapMemory.Max"));
                homePage.getNamespace().shouldHave(or("", exactText("local"),
                        exactText("ci")));
                homePage.getDimensions().shouldHave(text(
                        "AutoScalingGroupName:atscGroup"));

                // 証跡取得
                screenshot("testStartListen");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }
}
