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

import com.codeborne.selenide.Configuration;
import jp.co.ntt.cloud.functionaltest.selenide.page.ConfirmTokenPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.CustomErrorPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoggingPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.LoginPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.ShowCustomViewPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CwapTest {

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

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /*
     * (注：回帰試験によるログファイルの堆積により、試験時間の延伸が懸念されるため、手動で実行すること）
     * Spring Bootによるログファイルパス指定と、カスタムLogback定義ファイル(appName-logback-spring.xml)の
     * アプリケーションログ出力の確認を行う。
     */
//    @Test
    public void testApplicationLog() {

        // 準備 ログイン
        LoggingPage loggingPage = open(applicationContextUrl, LoginPage.class)
                .login().logging();

        final String uuid = UUID.randomUUID().toString();

        screenshot("manualTestApplicationLog");

        // テスト実行
        loggingPage = loggingPage.send(uuid);

        // アサーション
        assertLog(uuid);

        // ログアウト
        loggingPage.logout();
    }

    private void assertLog(String uuid) {
        final File f = new File("/var/log/applogs/cwap/spring.log");
        final String searchText = "outputUUID=" + uuid;
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(new FileInputStream(f),
                        Charset.forName("UTF-8")))) {
            String line = "";
            do {
                if (line.contains(searchText)) {
                    return;
                }
                line = r.readLine();
            } while (line != null);
            throw new IllegalStateException(
                    "Can't find target text:" + searchText);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * トランザクショントークンの正常遷移確認。
     */
    @Test
    public void testTransactionTokenCheckNormal() {

        // テスト実行
        ConfirmTokenPage confirmTokenPage = open(applicationContextUrl,
                LoginPage.class).login().confirmToken();

        // アサーション
        assertThat(confirmTokenPage.getResult(), is("Token check is valid."));

        // 証跡取得
        screenshot("testTransactionTokenCheckNormal");

        // ログアウト
        confirmTokenPage.logout();
    }

    /*
     * トランザクショントークンチェックエラー発生を確認する。
     * 遷移前にトークンチェックを行う画面に対し、直接GETを発行する。
     */
    @Test
    public void testTransactionTokenCheckError() {
        // 準備 ログイン
        open(applicationContextUrl, LoginPage.class).login();

        //テスト実行
        open(applicationContextUrl + "confirmToken");

        // アサーション
        assertThat($(By.id("result")).text(), is("Token check is invalid."));

        // 証跡取得
        screenshot("testTransactionTokenCheckError");

        // ログアウト
        open(applicationContextUrl, TopPage.class).logout();
    }

    /*
     * サーブレットフィルタが1リクエスト処理内で重複実行されていないこと。
     */
    @Test
    public void testDuplicateCount() {

        // テスト実行
        TopPage topPage = open(applicationContextUrl, LoginPage.class).login();

        // アサーション
        assertThat(topPage.getCounter(), is("counter:1"));

        // 証跡取得
        screenshot("testDuplicateCount");

        // ログアウト
        topPage.logout();
    }

    /*
     * <mvc:view-resolver/>に<mvc:bean-name/>を追加し、カスタムビュー定義が
     * 使用可能であることを確認する。
     */
    @Test
    public void testShowCustomView() {
        // テスト実行
        ShowCustomViewPage showCustomViewPage = open(applicationContextUrl,
                LoginPage.class).login().showCustomView();

        // アサーション
        assertThat(showCustomViewPage.getViewName(), is("CustomView"));

        // 証跡取得
        screenshot("testShowCustomView");

        // ログアウト
        open(applicationContextUrl, TopPage.class).logout();
    }

    /*
     * ErrorPageFilterとWebMvcAutoConfiguration無効化によるエラー画面表示の確認。
     */
    @Test
    public void testConfirmSystemError() {
        // テスト実行
        CustomErrorPage customErrorPage = open(applicationContextUrl,
                LoginPage.class).login().customError();

        // アサーション
        assertThat(customErrorPage.getErrorMessage(), is("Cwap custom error message."));

        // 証跡取得
        screenshot("testConfirmSystemError");

        // ログアウト
        open(applicationContextUrl, TopPage.class).logout();
    }

}
