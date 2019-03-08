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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.DownloadPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.PrdiMainPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class PrdiTest {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * レポート出力パス
     */
    @Value("${path.report}")
    private String reportPath;

    /*
     * geckoドライバーバージョン
     */
    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    /*
     * ログイン
     */
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

        // ログイン
        open(applicationContextUrl, TopPage.class).login("0000000001",
                "aaaaa11111");

        // ログイン後画面の遷移待ち
        $("title").shouldHave(exactText("Home"));
    }

    /*
     * ログアウト
     */
    @After
    public void tearDown() {
        open(applicationContextUrl, PrdiMainPage.class).logout();
    }

    /*
     * S3による署名つきURLのファイルをダウンロードする。 オブジェクトキーは landscape/logo.jpg (既存)
     */
    @Test
    public void testNormalDownload() throws Exception {
        // テスト実行
        DownloadPage downloadPage = open(applicationContextUrl,
                PrdiMainPage.class).clickDownload().download(
                        "landscape/logo.jpg");

        // アサーション
        $("#status").should(exactText("load complete."));
        assertThat(downloadPage.getSelectedKey(), is("landscape/logo.jpg"));
        assertThat(downloadPage.getLocalBase64(), is(downloadPage
                .getS3Base64()));

        // 証跡取得
        screenshot("testNormalDownload");

    }

    /*
     * 有効期限30秒を超えた署名つきURLを使用したとき、ダウンロードに失敗すること。 オブジェクトキーは expire/logo.jpg (既存だがダウンロード不可)
     */
    @Test
    public void testExpiredFileDownload() throws Exception {
        // テスト実行
        DownloadPage downloadPage = open(applicationContextUrl,
                PrdiMainPage.class).clickDownload().download("expire/logo.jpg");

        // アサーション
        $("#status").should(exactText("load failure."));
        assertThat(downloadPage.getSelectedKey(), is("expire/logo.jpg"));

        // 証跡取得
        screenshot("testExpiredFileDownload");
    }

    /*
     * S3上に存在しないオブジェクトキーを使用してファイルダウンロードを行う。 オブジェクトキーは invalid-object-key.jpg (S3上に存在しない)
     */
    @Test
    public void testNotExistObjectKeyDownload() throws Exception {
        // テスト実行
        DownloadPage downloadPage = open(applicationContextUrl,
                PrdiMainPage.class).clickDownload().download(
                        "invalid-object-key.jpg");

        // アサーション
        $("#status").should(exactText("load failure."));
        assertThat(downloadPage.getSelectedKey(), is("invalid-object-key.jpg"));

        // 証跡取得
        screenshot("testNotExistObjectKeyDownload");
    }
}
