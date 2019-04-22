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

import com.codeborne.selenide.Configuration;
import jp.co.ntt.cloud.functionaltest.selenide.page.MlsnPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * メール送信機能確認テストケース。
 * @author NTT 電電太郎
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class MlsnTest {

    /*
     * 正常送信確認用アドレス
     */
    private static final String ADDRESS_DELIVERY = "success@simulator.amazonses.com";

    /*
     * バウンスメール確認用アドレス
     */
    private static final String ADDRESS_BOUNCE = "bounce@simulator.amazonses.com";

    /*
     * SimpleMailMessageを使用する。
     */
    private static final String KIND_SIMPLE = "simple";

    /*
     * MimeMessageHelperを使用する。
     */
    private static final String KIND_MIME = "mime";

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

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /*
     * SimpleMailMessageを使用し、通常送信が通知されること。
     */
    @Test
    public void testSimpleDelivery() {

        // テスト実行
        MlsnPage send = open(applicationContextUrl, MlsnPage.class).send(
                ADDRESS_DELIVERY, KIND_SIMPLE, "simple message");

        // 検証
        assertThat(send.notificationType(), is("Delivery"));
        assertThat(send.headers(), is(containsString(
                "To:success@simulator.amazonses.com")));
        assertThat(send.headers(), is(containsString("Subject:testmail")));

        // 証跡取得
        screenshot("testSimpleDelivery");
    }

    /*
     * SimpleMailMessageを使用し、バウンスメールが通知されること。
     */
    @Test
    public void testSimpleBounce() {

        // テスト実行
        MlsnPage send = open(applicationContextUrl, MlsnPage.class).send(
                ADDRESS_BOUNCE, KIND_SIMPLE, "simple message");

        // 検証
        assertThat(send.notificationType(), is("Bounce"));
        assertThat(send.headers(), is(containsString(
                "To:bounce@simulator.amazonses.com")));
        assertThat(send.headers(), is(containsString("Subject:testmail")));

        // 証跡取得
        screenshot("testSimpleBounce");
    }

    /*
     * MimeMessageHelperを使用し、通常送信が通知されること。
     */
    @Test
    public void testMimeDelivery() {

        // テスト実行
        MlsnPage send = open(applicationContextUrl, MlsnPage.class).send(
                ADDRESS_DELIVERY, KIND_MIME, "MIME Message.");

        // 検証
        assertThat(send.notificationType(), is("Delivery"));
        assertThat(send.headers(), is(containsString(
                "To:success@simulator.amazonses.com")));
        assertThat(send.headers(), is(containsString(
                "Subject:MIME Mail test")));

        // 証跡取得
        screenshot("testMimeDelivery");
    }

    /*
     * MimeMessageHelperを使用し、バウンスメールが通知されること。
     */
    @Test
    public void testMimeBounce() {

        // テスト実行
        MlsnPage send = open(applicationContextUrl, MlsnPage.class).send(
                ADDRESS_BOUNCE, KIND_MIME, "MIME Message.");

        // 検証
        assertThat(send.notificationType(), is("Bounce"));
        assertThat(send.headers(), is(containsString(
                "To:bounce@simulator.amazonses.com")));
        assertThat(send.headers(), is(containsString(
                "Subject:MIME Mail test")));

        // 証跡取得
        screenshot("testMimeBounce");
    }
}
