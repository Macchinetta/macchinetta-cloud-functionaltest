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
 */
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.codeborne.selenide.Configuration;
import jp.co.ntt.cloud.functionaltest.selenide.page.DownloadPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.PrdiMainPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("selenide.timeout", "60000");
        System.setProperty("selenide.pollingInterval", "500");
    }

    /*
     * ログイン
     */
    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // ログイン
        open(applicationContextUrl, TopPage.class)
                .login("0000000001", "aaaaa11111");
    }

    /*
     * ログアウト
     */
    @After
    public void tearDown() {
        open(applicationContextUrl, PrdiMainPage.class).logout();
    }

    /*
     * S3による署名つきURLのファイルをダウンロードする。
     * オブジェクトキーは landscape/logo.jpg (既存)
     */
    @Test
    public void testNormalDownload() throws Exception {
        // テスト実行
        DownloadPage downloadPage = open(applicationContextUrl, PrdiMainPage.class)
                .clickDownload()
                .download("landscape/logo.jpg");

        // アサーション
        assertThat(downloadPage.getStatus(), is("load complete."));
        assertThat(downloadPage.getSelectedKey(), is("landscape/logo.jpg"));
        assertThat(downloadPage.getLocalBase64(), is(downloadPage.getS3Base64()));

        // 証跡取得
        screenshot("testNormalDownload");

    }

    /*
     * 有効期限30秒を超えた署名つきURLを使用したとき、ダウンロードに失敗すること。
     * オブジェクトキーは expire/logo.jpg (既存だがダウンロード不可)
     */
    @Test
    public void testExpiredFileDownload() throws Exception {
        // テスト実行
        DownloadPage downloadPage = open(applicationContextUrl, PrdiMainPage.class)
                .clickDownload()
                .download("expire/logo.jpg");

        // アサーション
        assertThat(downloadPage.getStatus(), is("load failure."));
        assertThat(downloadPage.getSelectedKey(), is("expire/logo.jpg"));

        // 証跡取得
        screenshot("testExpiredFileDownload");
    }

    /*
     * S3上に存在しないオブジェクトキーを使用してファイルダウンロードを行う。
     * オブジェクトキーは  invalid-object-key.jpg (S3上に存在しない)
     *
     */
    @Test
    public void testNotExistObjectKeyDownload() throws Exception {
        // テスト実行
        DownloadPage downloadPage = open(applicationContextUrl, PrdiMainPage.class)
                .clickDownload()
                .download("invalid-object-key.jpg");

        // アサーション
        assertThat(downloadPage.getStatus(), is("load failure."));
        assertThat(downloadPage.getSelectedKey(), is("invalid-object-key.jpg"));

        // 証跡取得
        screenshot("testNotExistObjectKeyDownload");
    }
}
