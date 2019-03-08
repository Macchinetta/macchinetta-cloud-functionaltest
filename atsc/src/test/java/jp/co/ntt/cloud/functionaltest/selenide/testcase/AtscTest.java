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
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.AtscPage;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class AtscTest {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * レポートパス
     */
    @Value("${path.report}")
    private String reportPath;

    /*
     * geckoドライバーバージョン
     */
    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            FirefoxDriverManager.getInstance().version(geckodriverVersion)
                    .setup();
        }

        // ブラウザの設定
        Configuration.browser = WebDriverRunner.MARIONETTE;

        // タイムアウトの設定
        Configuration.timeout = 1200000;

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /*
     * しきい値超えアラームの発生を待ち、通知内容を画面に表示する。
     */
    @Test
    public void testStartListen() throws InterruptedException {

        // テスト実行
        AtscPage atscPage = open(applicationContextUrl, AtscPage.class);
        Thread.sleep(2000);
        atscPage.submit();

        // アサーション
        atscPage.getNewState().should(exactText("ALARM"));
        atscPage.getNewStateReason().should(text("Threshold Crossed"));
        atscPage.getMetricName().should(exactText("HeapMemory.Max"));
        atscPage.getNamespace().should(or("", exactText("local"), exactText(
                "ci")));
        atscPage.getDimensions().should(text("AutoScalingGroupName:atscGroup"));

        // 証跡取得
        screenshot("testStartListen");
    }
}
