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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class StaticContentsTest extends TestCase {

    /*
     * 画像ファイルを一時保存するフォルダ作成と削除を実行するルール
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /*
     * テスト名取得
     */
    @Rule
    public TestName testName = new TestName();

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * アプリケーションURL
     */
    @Value("${path.report}")
    private String reportPath;

    /*
     * テストデータ保存先
     */
    @Value("${path.testdata}")
    private String testDataPath;

    /*
     * geckoドライバーバージョン
     */
    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    /*
     * ユーザID
     */
    private String userId;

    /*
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

    /*
     * 画像の読み込みが完了していること
     */
    @Test
    public void imgLoadCompleteTest() {
        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        HelloPage helloWorldPage = open(applicationContextUrl, TopPage.class)
                .login(userId, password);

        // 画像の読み込みが完了した場合に js でページ内の要素を"Image loading is complete"に書き換えているのでチェックする
        helloWorldPage.getImgLoadState().shouldHave(text(
                "Image loading is complete"));
    }

    /*
     * CloudFrontから取得した画像とローカルで保持している画像が一致すること
     */
    @Test
    public void sameImgCheckTest() throws IOException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        HelloPage helloWorldPage = open(applicationContextUrl, TopPage.class)
                .login(userId, password);

        // 画像のURL取得
        String imgSrc = helloWorldPage.getImagFromCloudFront().getAttribute(
                "src");

        // CloudFrontから取得した画像の保存先
        String tempImgPath = temporaryFolder.getRoot() + "ochiboHiroi_temp.jpg";

        // CloudFrontから画像ダウンロード
        FileUtils.copyURLToFile(new URL(imgSrc), new File(tempImgPath));

        File imgFromLocal = new File(testDataPath + "image/ochiboHiroi.jpg");
        File imgFromCloudFront = new File(tempImgPath);

        // アサーション
        assertEquals(FileUtils.checksumCRC32(imgFromLocal), FileUtils
                .checksumCRC32(imgFromCloudFront));

        // 証跡取得
        screenshot(testName.getMethodName());
    }

    /*
     * 画像をCloudFrontのキャッシュから取得していること
     */
    @Test
    public void xCacheTest() throws IOException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        HelloPage helloWorldPage = open(applicationContextUrl, TopPage.class)
                .login(userId, password);

        // 画像のURL取得
        String imgSrc = helloWorldPage.getImagFromCloudFront().getAttribute(
                "src");

        CloseableHttpClient client = HttpClients.createDefault();

        // CloudFrontに画像をキャッシュさせるためにアクセス
        try (CloseableHttpResponse response = client.execute(
                new HttpGet(imgSrc))) {
        }

        // CloudFrontのキャッシュから画像を取得
        try (CloseableHttpResponse response = client.execute(
                new HttpGet(imgSrc))) {
            // X-Cache の値を取得
            String xCacheResult = response.getFirstHeader("X-Cache").getValue();

            if (StringUtils.isNotBlank(xCacheResult)) {
                assertEquals("Hit from cloudfront", xCacheResult);
            } else {
                fail();
            }
        }
    }
}
