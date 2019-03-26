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

import static com.codeborne.selenide.Condition.exactText;
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
import jp.co.ntt.cloud.functionaltest.selenide.page.UserManagementPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class ReadReplicaTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    // 顧客名：姓
    private String lastName;

    // 顧客名：名
    private String firstName;

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
     * RDRP0201 001 レプリカデータベースからデータを取得できること
     */
    @Test
    public void testRDRP0201001() {

        // テスト実行:参照系トランザクションの処理を行う
        UserManagementPage userManagementPage = open(applicationContextUrl,
                UserManagementPage.class);

        // アサート:顧客テーブルに初期投入データが表示されていることを確認する。
        userManagementPage.getLastNameColumn(1).shouldHave(exactText("Denden"));
        userManagementPage.getFirstNameColumn(1).shouldHave(exactText(
                "Hanako"));
        userManagementPage.getLastNameColumn(2).shouldHave(exactText("Denden"));
        userManagementPage.getFirstNameColumn(2).shouldHave(exactText("Taro"));

        // 証跡取得
        screenshot("testRDRP0201001");
    }

    /**
     * RDRP0201 002 マスタデータベースにデータを更新できること
     */
    @Test
    public void testRDRP0201002() {

        // 事前準備
        lastName = "Toyosu";
        firstName = "Yoshiko";

        // テスト実行:更新系トランザクションの処理を行う
        UserManagementPage userManagementPage = open(applicationContextUrl,
                UserManagementPage.class).register(lastName, firstName)
                        .reload();

        // アサート:顧客テーブルに顧客情報が登録されていることを確認する。
        // テーブルの行数を取得し最後のデータを確認する。
        int maxNumberOfRow = userManagementPage.getRows().size() - 1;
        userManagementPage.getLastNameColumn(maxNumberOfRow).shouldHave(
                exactText(lastName));
        userManagementPage.getFirstNameColumn(maxNumberOfRow).shouldHave(
                exactText(firstName));

        // 証跡取得
        screenshot("testRDRP0201002");
    }
}
