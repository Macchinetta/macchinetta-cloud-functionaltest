/*
 * Copyright 2014-2017 NTT Corporation.
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

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;
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

import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class ReadReplicaTest extends TestCase {

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
     * 顧客名：姓
     */
    private String lastName;

    /*
     * 顧客名：名
     */
    private String firstName;

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }
 
    @After
    public void tearDown() {
    }

    /**
     * <ul>
     * <li>レプリカデータベースからデータを取得できることを確認する。</li>
     * </ul>
     */
    @Test
    public void testRDRP0101001() {

        // 事前準備
        lastName = "0000000002";
        firstName = "aaaaa11111";

        // テスト実行
        open(applicationContextUrl, TopPage.class);

        // アサーション
        
        // 顧客テーブルに初期投入データが表示されていることを確認する。
        $(byId("userListTable")).$("tr", 1).$(".lastName").shouldBe(text("Denden"));
        $(byId("userListTable")).$("tr", 1).$(".firstName").shouldBe(text(
                "Hanako"));
        $(byId("userListTable")).$("tr", 2).$(".lastName").shouldBe(text("Denden"));
        $(byId("userListTable")).$("tr", 2).$(".firstName").shouldBe(text(
                "Taro"));

        // 証跡取得
        screenshot("testRDRP0101001");
    }

    /**
     * <ul>
     * <li>マスタデータベースにデータを更新できることを確認する。</li>
     * </ul>
     */
    @Test
    public void testRDRP0101002() {

        // 事前準備
        lastName = "Toyosu";
        firstName = "Yoshiko";

        // テスト実行
        open(applicationContextUrl, TopPage.class)
        .register(lastName, firstName)
        .reload();

        // アサーション
        // 顧客テーブルに顧客情報が登録されていることを確認する。

        // テーブルの行数を取得し最後のデータを確認する。
        int totalNumberOfRow = $(byId("userListTable")).$$("tr").size();
        int maxNumberOfRow = totalNumberOfRow - 1;
        $(byId("userListTable")).$("tr", maxNumberOfRow).$(".lastName")
                .shouldBe(text(lastName));
        $(byId("userListTable")).$("tr", maxNumberOfRow).$(".firstName")
                .shouldBe(text(firstName));

        // 証跡取得
        screenshot("testRDRP0101002");
    }
}
