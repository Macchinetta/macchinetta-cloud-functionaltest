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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.CwapCustomErrorPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoggingPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.ShowCustomViewPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TokenCheckPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CwapTest {

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

    /**
     * CWAP0101 001 正常 Logbackの拡張ログが出力できることを確認する。 <br>
     * (注：回帰試験によるログファイルの堆積により、試験時間の延伸が懸念されるため、手動で実行すること）
     */
    // @Test
    public void testApplicationLog() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // 事前準備:ログイン
                LoggingPage loggingPage = open(applicationContextUrl,
                        LoginPage.class).login().logging();

                final String uuid = UUID.randomUUID().toString();

                screenshot("manualTestApplicationLog");

                // テスト実行:UUIDを送信する。
                loggingPage = loggingPage.send(uuid);

                // アサート:アプリケーションログに「outputUUID=59143879-8750-47e1-bfe1-eb7d73e44f6d」がINFOレベルで出力されていること。
                assertLog(uuid);

                // ログアウト
                loggingPage.logout();
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    private void assertLog(String uuid) {
        final File f = new File("/var/log/applogs/cwap/spring.log");
        final String searchText = "outputUUID=" + uuid;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), Charset
                .forName("UTF-8")))) {
            String line = "";
            do {
                if (line.contains(searchText)) {
                    return;
                }
                line = r.readLine();
            } while (line != null);
            throw new IllegalStateException("Can't find target text:"
                    + searchText);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * CWAP0102 001 トランザクショントークンチェックが使用できることを確認する。（トークンチェック正常）
     */
    @Test
    public void testTransactionTokenCheckNormal() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:トークン発行、トークンの一致チェックのリクエストを発行する。
                TokenCheckPage tokenCheckPage = open(applicationContextUrl,
                        LoginPage.class).login().generateToken().confirmToken();

                // アサート:トークンチェックが正常と判断され、次ページへの遷移が行われていること。
                tokenCheckPage.getResult().shouldHave(exactText(
                        "Token check is valid."));

                // 証跡取得
                screenshot("testTransactionTokenCheckNormal");

                // ログアウト
                tokenCheckPage.logout();
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * CWAP0102 002 トランザクショントークンチェックが使用できることを確認する。（トークンチェック異常）
     */
    @Test
    public void testTransactionTokenCheckError() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:トークン発行せずにトークンの一致チェックのリクエストを発行する。
                TokenCheckPage tokenCheckPage = open(applicationContextUrl,
                        LoginPage.class).login().confirmToken();

                // アサート:トークンチェックが異常と判断され、エラーページへ遷移すること。
                tokenCheckPage.getResult().shouldHave(exactText(
                        "Token check is invalid."));

                // 証跡取得
                screenshot("testTransactionTokenCheckError");

                // ログアウト
                open(applicationContextUrl, HomePage.class).logout();
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * CWAP0103 001 Filterの二重登録が抑止できることを確認する。
     */
    @Test
    public void testDuplicateCount() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:ログインする。
                HomePage homePage = open(applicationContextUrl, LoginPage.class)
                        .login();

                // アサート:サーブレットフィルタの二重実行が抑止され、COUNTER_KEYの値が1であることを確認する。
                homePage.getCounter().shouldHave(exactText("counter:1"));

                // 証跡取得
                screenshot("testDuplicateCount");

                // ログアウト
                homePage.logout();
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * CWAP104 001 <mvc:view-resolvers>によるViewResolver定義が使用できることを確認する。
     */
    @Test
    public void testShowCustomView() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:カスタムViewを返却するレスポンスを返却する。
                ShowCustomViewPage showCustomViewPage = open(
                        applicationContextUrl, LoginPage.class).login()
                                .showCustomView();

                // アサート:レスポンスボディにCustomViewが含まれていることを確認する。
                showCustomViewPage.getViewName().shouldHave(exactText(
                        "CustomView"));

                // 証跡取得
                screenshot("testShowCustomView");

                // ログアウト
                open(applicationContextUrl, HomePage.class).logout();
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * CWAP0105 001 システムエラー発生時に画面が白抜けせず、システムエラー画面が表示できることを確認する。
     */
    @Test
    public void testConfirmSystemError() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:コントローラ内部でRuntimeExceptionを継承する、任意のシステムエラーを発生させる。
                CwapCustomErrorPage cwapCustomErrorPage = open(
                        applicationContextUrl, LoginPage.class).login()
                                .customError();

                // アサート:システムエラー画面に遷移することを確認する。
                cwapCustomErrorPage.getErrorMessage().shouldHave(exactText(
                        "Cwap custom error message."));

                // 証跡取得
                screenshot("testConfirmSystemError");

                // ログアウト
                open(applicationContextUrl, HomePage.class).logout();
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

}
