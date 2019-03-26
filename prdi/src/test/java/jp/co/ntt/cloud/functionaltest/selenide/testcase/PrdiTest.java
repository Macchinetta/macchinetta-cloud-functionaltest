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
import jp.co.ntt.cloud.functionaltest.selenide.page.IndexPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class PrdiTest {

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

        // ログイン
        // サスペンド:画面遷移確認
        open(applicationContextUrl, LoginPage.class).login("0000000001",
                "aaaaa11111").getH().shouldHave(exactText("Hello world!"));
    }

    @After
    public void tearDown() {
        open(applicationContextUrl, HomePage.class).logout();
    }

    /**
     * PRDI0101 001 S3バケットから署名付きURLにてダウンロードできることを確認する。
     */
    @Test
    public void testNormalDownload() {

        // テスト実行:ファイルをダウンロードする。
        IndexPage indexPage = open(applicationContextUrl, HomePage.class)
                .clickDownload().download("landscape/logo.jpg");

        // アサート:S3バケットから署名付きURLにてダウンロードできること、APサーバ上のローカルファイルとBASE64データが一致することをもってダウンロード正常確認とする。
        indexPage.getStatus().shouldHave(exactText("load complete."));
        indexPage.getSelectedKey().shouldHave(exactText("landscape/logo.jpg"));
        indexPage.getLocalBase64().shouldHave(exactText(indexPage.getS3Base64()
                .getText()));

        // 証跡取得
        screenshot("testNormalDownload");

    }

    /**
     * PRDI0101 002 有効期限切れの署名付きURLではダウンロードできないことを確認する。
     */
    @Test
    public void testExpiredFileDownload() {

        // テスト実行:ファイルをダウンロードする。
        IndexPage indexPage = open(applicationContextUrl, HomePage.class)
                .clickDownload().download("expire/logo.jpg");

        // アサート:ブラウザのダウンロードに失敗すること、<img>タグのerror()イベントを捕捉することでダウンロード失敗とする。
        indexPage.getStatus().shouldHave(exactText("load failure."));
        indexPage.getSelectedKey().shouldHave(exactText("expire/logo.jpg"));

        // 証跡取得
        screenshot("testExpiredFileDownload");
    }

    /**
     * PRDI0101 003 S3バケット上に存在しないオブジェクトキーを指定して、ファイルダウンロードに失敗することを確認する。
     */
    @Test
    public void testNotExistObjectKeyDownload() {

        // テスト実行:ファイルをダウンロードする。
        IndexPage indexPage = open(applicationContextUrl, HomePage.class)
                .clickDownload().download("invalid-object-key.jpg");

        // アサート:ブラウザのダウンロードに失敗すること、<img>タグのerror()イベントを捕捉することでダウンロード失敗とする。
        indexPage.getStatus().shouldHave(exactText("load failure."));
        indexPage.getSelectedKey().shouldHave(exactText(
                "invalid-object-key.jpg"));

        // 証跡取得
        screenshot("testNotExistObjectKeyDownload");
    }
}
