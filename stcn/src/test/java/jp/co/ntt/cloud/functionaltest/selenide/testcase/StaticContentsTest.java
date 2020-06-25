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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
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
public class StaticContentsTest {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${path.testdata}")
    private String testDataPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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

    @After
    public void tearDown() {

        // ログイン状態の場合ログアウトする。
        HomePage homePage = open(applicationContextUrl, HomePage.class);
        if (homePage.isLoggedIn()) {
            homePage.logout();
        }
    }

    /**
     * STCN0101 001 CloudFrontから画像を取得し、JSPで表示できること
     */
    @Test
    public void imgLoadCompleteTest() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // テスト実行:ログインして静的コンテンツを取得する
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login("0000000002", "aaaaa11111");

                // アサート:画像が表示されていることを確認する。
                homePage.getImgLoadState().shouldHave(exactText(
                        "Image loading is complete"));
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }

    /**
     * STCN0101 002 CloudFrontから取得した画像が、想定している画像と一致すること
     * @throws IOException
     * @throws MalformedURLException
     */
    @Test
    public void sameImgCheckTest() throws MalformedURLException, IOException {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // テスト実行:ログインして静的コンテンツを取得する
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login("0000000002", "aaaaa11111");

                // 画像のURL取得
                String imgSrc = homePage.getImagFromCloudFront().getAttribute(
                        "src");

                // CloudFrontから取得した画像の保存先
                String tempImgPath = temporaryFolder.getRoot()
                        + "ochiboHiroi_temp.jpg";

                // CloudFrontから画像ダウンロード
                FileUtils.copyURLToFile(new URL(imgSrc), new File(tempImgPath));

                File imgFromLocal = new File(testDataPath
                        + "image/ochiboHiroi.jpg");
                File imgFromCloudFront = new File(tempImgPath);

                // アサート:CloudFrontから配信された画像が想定している画像と一致していることを確認する。
                assertEquals(FileUtils.checksumCRC32(imgFromLocal), FileUtils
                        .checksumCRC32(imgFromCloudFront));

                // 証跡取得
                screenshot(testName.getMethodName());
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }

    /**
     * STCN0102 001 CloudFrontのキャッシュから画像を取得していること
     * @throws IOException
     * @throws ClientProtocolException
     */
    @Test
    public void xCacheTest() throws ClientProtocolException, IOException {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // テスト実行:ログインして静的コンテンツを取得する
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login("0000000002", "aaaaa11111");

                // 画像のURL取得
                String imgSrc = homePage.getImagFromCloudFront().getAttribute(
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
                    String xCacheResult = response.getFirstHeader("X-Cache")
                            .getValue();

                    if (StringUtils.isNotBlank(xCacheResult)) {

                        // アサート:レスポンスヘッダの「X-Cache」が「Hit from CloudFront」になっていることを確認する。
                        assertEquals("Hit from cloudfront", xCacheResult);
                    } else {
                        fail();
                    }
                }
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }
}
