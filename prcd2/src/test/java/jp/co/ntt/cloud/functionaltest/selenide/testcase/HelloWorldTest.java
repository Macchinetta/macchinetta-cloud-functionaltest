/*
 * Copyright(c) 2017 NTT Corporation.
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

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class HelloWorldTest {

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
     * ユーザID
     */
    private String userId;

    /*
     * パスワード
     */
    private String password;

    @Before
    public void setUp() {
        // ブラウザの設定
        Configuration.browser = "jp.co.ntt.cloud.functionaltest.selenide.testcase.FirefoxWebDriverProvider";

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @After
    public void tearDown() {
        // ログイン状態の場合ログアウトする。
        HelloPage helloPage = open(applicationContextUrl, HelloPage.class);
        if (helloPage.isLoggedIn()) {
            helloPage.logout();
        }
    }

    /*
     * PRCD20101_001 署名付きCookieを利用して、アクセス制限が設定されているCloudFront上のファイルにIP制限に因ってアクセスできないことを確認する
     */
    @Test
    public void testPRCD20101_001() throws InterruptedException {

        // 事前準備
        userId = "0000000002";
        password = "aaaaa11111";

        // テスト実行
        TopPage topPage = open(applicationContextUrl, TopPage.class);

        HelloPage helloWorldPage = topPage.login(userId, password);

        // アサーション
        $("h1").shouldHave(text("Hello world!"));
        $$("p").get(1).shouldHave(text("Taro Denden"));

        // コンテンツの閲覧期限に達したので閲覧可能だがIP制限で閲覧不可
        Thread.sleep(5000);

        helloWorldPage.loadVerificationContent();

        Thread.sleep(10000);

        String cloudfrontVal = $(byId("cloudFrontResult")).val();
        assertThat(cloudfrontVal, isEmptyOrNullString());

        // 証跡取得
        screenshot("PRCD20101_001");

    }

}
