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
public class MlsnTest {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    // 正常送信確認用アドレス
    private static final String ADDRESS_DELIVERY = "success@simulator.amazonses.com";

    // バウンスメール確認用アドレス
    private static final String ADDRESS_BOUNCE = "bounce@simulator.amazonses.com";

    // SimpleMailMessageを使用する。
    private static final String KIND_SIMPLE = "simple";

    // MimeMessageHelperを使用する。
    private static final String KIND_MIME = "mime";

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
     * MLSN0101 001 正常メッセージの送信確認
     */
    @Test
    public void testSimpleDelivery() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // テスト実行:メールを送信する。
                HomePage homePage = open(applicationContextUrl, HomePage.class)
                        .send(ADDRESS_DELIVERY, KIND_SIMPLE, "simple message");

                // アサート:以下情報を含む通知が返却されること。
                // NotificationType:Delivery
                // To:success@simulator.amazonses.com
                // Subject:testmail
                homePage.getNotificationType().shouldHave(exactText(
                        "Delivery"));
                homePage.getHeaders().shouldHave(text(
                        "To:success@simulator.amazonses.com"));
                homePage.getHeaders().shouldHave(text("Subject:testmail"));

                // 証跡取得
                screenshot("testSimpleDelivery");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }

    /**
     * MLSN0101 002 バウンスメールの通知確認
     */
    @Test
    public void testSimpleBounce() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:メールを送信する。
                HomePage homePage = open(applicationContextUrl, HomePage.class)
                        .send(ADDRESS_BOUNCE, KIND_SIMPLE, "simple message");

                // アサート:以下情報を含む通知が返却されること。
                // NotificationType:Bounce
                // To:bounce@simulator.amazonses.com
                // Subject:testmail
                homePage.getNotificationType().shouldHave(exactText("Bounce"));
                homePage.getHeaders().shouldHave(text(
                        "To:bounce@simulator.amazonses.com"));
                homePage.getHeaders().shouldHave(text("Subject:testmail"));

                // 証跡取得
                screenshot("testSimpleBounce");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }

    }

    /**
     * MLSN0201 001 正常メッセージの送信確認
     */
    @Test
    public void testMimeDelivery() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {

                // テスト実行:メールを送信する。
                HomePage homePage = open(applicationContextUrl, HomePage.class)
                        .send(ADDRESS_DELIVERY, KIND_MIME, "MIME Message.");

                // アサート:以下情報を含む通知が返却されること。
                // NotificationType:Delivery
                // To:success@simulator.amazonses.com
                // Subject:MIME Mail test
                homePage.getNotificationType().shouldHave(exactText(
                        "Delivery"));
                homePage.getHeaders().shouldHave(text(
                        "To:success@simulator.amazonses.com"));
                homePage.getHeaders().shouldHave(text(
                        "Subject:MIME Mail test"));

                // 証跡取得
                screenshot("testMimeDelivery");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }

    /**
     * MLSN0201 002 バウンスメールの通知確認
     */
    @Test
    public void testMimeBounce() {

        for (int retryCount = 0; retryCount < 100; retryCount++) {
            try {
                // テスト実行:メールを送信する。
                HomePage homePage = open(applicationContextUrl, HomePage.class)
                        .send(ADDRESS_BOUNCE, KIND_MIME, "MIME Message.");

                // アサート:以下情報を含む通知が返却されること。
                // NotificationType:Bounce
                // To:bounce@simulator.amazonses.com
                // Subject:MIME Mail test
                homePage.getNotificationType().shouldHave(exactText("Bounce"));
                homePage.getHeaders().shouldHave(text(
                        "To:bounce@simulator.amazonses.com"));
                homePage.getHeaders().shouldHave(text(
                        "Subject:MIME Mail test"));

                // 証跡取得
                screenshot("testMimeBounce");
                break;
            } catch (Exception e) {
                // エラー時はリトライ
            }
        }
    }
}
