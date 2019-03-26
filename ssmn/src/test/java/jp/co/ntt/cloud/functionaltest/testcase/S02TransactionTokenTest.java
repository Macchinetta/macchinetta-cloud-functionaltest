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
package jp.co.ntt.cloud.functionaltest.testcase;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.CreateOutputPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.CustomStoreSizeNextPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow1NamespaceStep2Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow1NamespaceStep3Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow1Step2Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow1Step2ToDifferentNamespacePage;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow1Step3Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow2Step2Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.Flow2Step3Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.FlowAllStep1Page;
import jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken.IndexPage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class S02TransactionTokenTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${until.session.timeout.sec}")
    private Integer untilSessionTimeout;

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

    @AfterClass
    static public void exitDriver() {
        WebDriverRunner.closeWebDriver();
    }

    /**
     * SSMN0101 001 クラスに対して、指定したvalueの値で、Namespaceが有効になっていること
     */
    @Test
    public void testSSMN0101001() {

        // テスト実行
        CreateOutputPage createOutputPage = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink1().clickBtn1();

        // アサート:「transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}」の形であることを確認する。
        createOutputPage.getResult().shouldHave(matchText(
                "transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0101001");

    }

    /**
     * SSMN0101 002 クラスとメソッドに対して、指定したvalueの値で、Namespaceが有効になっていること
     */
    @Test
    public void testSSMN0101002() {

        // テスト実行
        CreateOutputPage createOutputPage = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink1().clickBtn2();

        // アサート:「transactiontoken/create~[0-9a-z]{32}~[0-9a-z]{32}」の形であることを確認する。
        createOutputPage.getResult().shouldHave(matchText(
                "transactiontoken/create~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0101002");
    }

    /**
     * SSMN0101 003 メソッドに対して、指定したvalueの値で、Namespaceが有効になっていること
     */
    @Test
    public void testSSMN0101003() {

        // テスト実行
        CreateOutputPage createOutputPage = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink1().clickBtn3();

        // アサート:「create~[0-9a-z]{32}~[0-9a-z]{32}」の形であることを確認する。
        createOutputPage.getResult().shouldHave(matchText(
                "create~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0101003");
    }

    /**
     * SSMN0101 004 クラスとメソッド両方に対して、valueの値は指定していないので、globalTokenでNamespaceが有効になっていること
     */
    @Test
    public void testSSMN0101004() {

        // テスト実行
        CreateOutputPage createOutputPage = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink1().clickBtn4();

        // アサート:「globalToken~[0-9a-z]{32}~[0-9a-z]{32}」の形であることを確認する。
        createOutputPage.getResult().shouldHave(matchText(
                "globalToken~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0101004");
    }

    /**
     * SSMN0102 001 BEGIN-IN-CHECK-END (Namespace指定している - value属性)
     */
    @Test
    public void testSSMN0102001() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102001-001");

        String currentToken = flow1Step2Page.getTransactionToken().getValue();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:BEGINメソッドで作られたtransaction tokenと、 INメソッドで作られたtransaction tokenの比較を行い、
        // name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        flow1Step3Page.getResult().shouldHave(matchText("^(?!.*" + currentToken
                .split("~")[2] + ").*(?=" + currentToken.split("~")[0] + "~"
                + currentToken.split("~")[1] + ").*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102001-002");

        currentToken = flow1Step3Page.getTransactionToken().getValue();

        // アサート:CHECKのついているメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていること（更新されていない）
        flow1Step3Page.clickBtnCheck().getTransactionToken().shouldHave(value(
                currentToken));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102001-003");

        // アサート:ENDのついているメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていないこと（破棄されている）
        flow1Step3Page.clickBtnEnd().getTransactionToken().shouldNotHave(exist);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102001-004");

    }

    /**
     * SSMN0102 002 BEGIN-IN-CHECK-END (Namespace指定している - namespace属性)
     */
    @Test
    public void testSSMN0102002() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1NamespaceStep2Page flow1NamespaceStep2Page = open(
                applicationContextUrl + "transactiontoken", IndexPage.class)
                        .clickLink5().clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1NamespaceStep2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102002-001");

        String currentToken = flow1NamespaceStep2Page.getTransactionToken()
                .getValue();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1NamespaceStep3Page flow1NamespaceStep3Page = flow1NamespaceStep2Page
                .clickBtnIn();

        // アサート:BEGINメソッドで作られたtransaction tokenと、 INメソッドで作られたtransaction tokenの比較を行い、
        // name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        flow1NamespaceStep3Page.getResult().shouldHave(matchText("^(?!.*"
                + currentToken.split("~")[2] + ").*(?=" + currentToken.split(
                        "~")[0] + "~" + currentToken.split("~")[1] + ").*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102002-002");

        currentToken = flow1NamespaceStep3Page.getTransactionToken().getValue();

        // アサート:CHECKのついているメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていること（更新されていない）
        flow1NamespaceStep3Page.clickBtnCheck().getTransactionToken()
                .shouldHave(value(currentToken));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102002-003");

        // アサート:ENDのついているメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていないこと（破棄されている）
        flow1NamespaceStep3Page.clickBtnEnd().getTransactionToken()
                .shouldNotHave(exist);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102002-004");

    }

    /**
     * SSMN0102 003 BEGIN-IN-CHECK-END (Namespace指定していない - globalToken)
     */
    @Test
    public void testSSMN0102003() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow2Step2Page flow2Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow2();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow2Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102003-001");

        String currentToken = flow2Step2Page.getTransactionToken().getValue();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow2Step3Page flow2Step3Page = flow2Step2Page.clickBtnIn();

        // アサート:BEGINメソッドで作られたtransaction tokenと、 INメソッドで作られたtransaction tokenの比較を行い、
        // name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        // 作成されたtransaction tokenのnameは"globalToken"であること
        flow2Step3Page.getResult().shouldHave(text("globalToken"), matchText(
                "^(?!.*" + currentToken.split("~")[2] + ").*(?=" + currentToken
                        .split("~")[0] + "~" + currentToken.split("~")[1]
                        + ").*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102003-002");

        currentToken = flow2Step3Page.getTransactionToken().getValue();

        // アサート:CHECKのついているメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていること（更新されていない）
        flow2Step3Page.clickBtnCheck().getTransactionToken().shouldHave(value(
                currentToken));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102003-003");

        // アサート:ENDのついているメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていないこと（破棄されている）
        flow2Step3Page.clickBtnEnd().getTransactionToken().shouldNotHave(exist);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102003-004");
    }

    /**
     * SSMN0102 004 BEGIN-END
     */
    @Test
    public void testSSMN0102004() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102004-001");

        // アサート:ENDメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていないこと（破棄されている）
        flow1Step2Page.clickBtnEnd().getTransactionToken().shouldNotHave(exist);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102004-002");
    }

    /**
     * SSMN0102 005 BEGIN-IN-IN(Redo)-IN-END
     */
    @Test
    public void testSSMN0102005() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102005-001");

        String currentToken = flow1Step2Page.getTransactionToken().getValue();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:BEGINメソッドで作られたtransaction tokenと、INメソッドで作られたtransaction
        // tokenの比較を行い、name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        flow1Step3Page.getResult().shouldHave(matchText("^(?!.*" + currentToken
                .split("~")[2] + ").*(?=" + currentToken.split("~")[0] + "~"
                + currentToken.split("~")[1] + ").*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102005-002");

        currentToken = flow1Step3Page.getTransactionToken().getValue();

        // テスト実行(IN(Redo)のついているメソッドに対するリクエスト)
        flow1Step2Page = flow1Step3Page.clickBtnBack();

        // アサート:BEGINメソッドで作られたtransaction tokenと、INメソッドで作られたtransaction
        // tokenの比較を行い、name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        flow1Step2Page.getResult().shouldHave(matchText("^(?!.*" + currentToken
                .split("~")[2] + ").*(?=" + currentToken.split("~")[0] + "~"
                + currentToken.split("~")[1] + ").*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102005-003");

        // アサート:ENDメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていないこと（破棄されている）
        flow1Step2Page.clickBtnEnd().getTransactionToken().shouldNotHave(exist);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102005-004");
    }

    /**
     * SSMN0102 006 BEGIN(Input Error)-BEGIN-IN
     */
    @Test
    public void testSSMN0102006() {

        // テスト実行(BEGIN(Input Error)のついているメソッドに対するリクエスト)
        FlowAllStep1Page flowAllStep1Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow5_1();

        // アサート:入力チェックエラーが発生してもBEGINメソッドに対するリクエストのレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flowAllStep1Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102006-001");

        String currentToken = flowAllStep1Page.getTransactionToken().getValue();

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = flowAllStep1Page.clickBtnFlow5_2();

        // アサート:入力チェックエラーを解決してもう一度リクエストを出したらレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102006-002");

        currentToken = flow1Step2Page.getTransactionToken().getValue();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:BEGINメソッドで作られたtransaction tokenと、INメソッドで作られたtransaction
        // tokenの比較を行い、name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        flow1Step3Page.getResult().shouldHave(matchText("^(?!.*(" + currentToken
                .split("~")[1] + "|" + currentToken.split("~")[2] + ").*(?="
                + currentToken.split("~")[0] + ")).*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102006-003");
    }

    /**
     * SSMN0102 007 BEGIN-END (Business Error)
     */
    @Test
    public void testSSMN0102007() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102007-001");

        // アサート:ENDメソッドに対するリクエストの処理でエラーが発生してもレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていないこと（破棄されている）
        flow1Step2Page.clickBtnEndError().getTransactionToken().shouldNotHave(
                exist);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102007-002");
    }

    /**
     * SSMN0102 008 IN called without BEGIN (Token error since token not present)
     */
    @Test
    public void testSSMN0102008() {

        // アサート: INメソッドに対するリクエストの呼び出しでトークンが渡されていないのでtoken errorが発生されている
        open(applicationContextUrl + "transactiontoken", IndexPage.class)
                .clickLink2().clickBtnFlow3().getH().shouldHave(exactText(
                        "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102008-001");
    }

    /**
     * SSMN0102 009 BEGIN-IN-(Browser Back)-IN (Token error due to Token mismatch)
     */
    @Test
    public void testSSMN0102009() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102009-001");

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（更新されている）
        flow1Step3Page.getTransactionToken().shouldNotHave(empty);

        // テスト実行(BrowserのBackを押下)
        Selenide.back();

        // アサート:サブミットするとtoken errorが発生されている
        flow1Step2Page.clickBtnEnd().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102009-002");
    }

    /**
     * SSMN0102 010 BEGIN-IN (Token error due to Token mismatch)
     */
    @Test
    public void testSSMN0102010() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2ToDifferentNamespacePage flow1Step2ToDifferentNamespacePage = open(
                applicationContextUrl + "transactiontoken", IndexPage.class)
                        .clickLink2().clickBtnFlow7();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2ToDifferentNamespacePage.getTransactionToken().shouldNotHave(
                empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102010-001");

        // アサート:INメソッドに対するリクエストの呼び出しで渡されたトークンが一致していないのでtoken errorが発生されている
        flow1Step2ToDifferentNamespacePage.clickBtnIn().getH().shouldHave(
                exactText("Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102010-002");

    }

    /**
     * SSMN0102 011 END called without BEGIN (Token error since token not present)
     */
    @Test
    public void testSSMN0102011() {

        // アサート:ENDメソッドに対するリクエストの呼び出しでトークンが渡されていないのでtoken errorが発生されている
        open(applicationContextUrl + "transactiontoken", IndexPage.class)
                .clickLink2().clickBtnFlow4().getH().shouldHave(exactText(
                        "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102011-001");
    }

    /**
     * SSMN0102 012 BEGIN-END (Token error due to Token mismatch)
     */
    @Test
    public void testSSMN0102012() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2ToDifferentNamespacePage flow1Step2ToDifferentNamespacePage = open(
                applicationContextUrl + "transactiontoken", IndexPage.class)
                        .clickLink2().clickBtnFlow7();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2ToDifferentNamespacePage.getTransactionToken().shouldNotHave(
                empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102012-001");

        // アサート:ENDメソッドに対するリクエストの呼び出しで渡されたトークンが一致していないのでtoken errorが発生されている
        flow1Step2ToDifferentNamespacePage.clickBtnEnd().getH().shouldHave(
                exactText("Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102012-002");
    }

    /**
     * SSMN0102 013 BEGIN-IN
     */
    @Test
    public void testSSMN0102013() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102013-001");

        String currentToken = flow1Step2Page.getTransactionToken().getValue();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:BEGINメソッドで作られたtransaction tokenと、INメソッドで作られたtransaction
        // tokenの比較を行い、name、keyは一致し、valueは一致していないこと(特定部分のみ更新されていること)
        flow1Step3Page.getResult().shouldHave(matchText("^(?!.*" + currentToken
                .split("~")[2] + ").*(?=" + currentToken.split("~")[0] + "~"
                + currentToken.split("~")[1] + ").*$"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102013-002");
    }

    /**
     * SSMN0102 014 BEGIN-CHECK(File Download)-IN
     */
    @Test
    public void testSSMN0102014() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102014-001");

        // アサート:テスト実行(CHECK(File Download)のついているメソッドに対するリクエスト)
        flow1Step2Page.clickBtnDownload01();

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:INのついているメソッドの実行時にエラーが発生しないこと
        flow1Step3Page.getH().shouldNotHave(exactText(
                "Transaction Token Error"));

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（更新されている）
        flow1Step3Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102014-002");

    }

    /**
     * SSMN0102 015 BEGIN-IN-(Browser Back)-CHECK
     */
    @Test
    public void testSSMN0102015() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102015-001");

        // テスト実行(INのついているメソッドに対するリクエスト)
        Flow1Step3Page flow1Step3Page = flow1Step2Page.clickBtnIn();

        // アサート:INのついているメソッドの実行時にエラーが発生しないこと
        flow1Step3Page.getH().shouldNotHave(exactText(
                "Transaction Token Error"));

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（更新されている）
        flow1Step3Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102015-002");

        // テスト実行(BrowserのBackを押下)
        Selenide.back();

        // アサート:CHECKのついているメソッドに対するリクエストの呼び出しでトークンが一致していないのでtoken errorが発生する
        flow1Step2Page.clickBtnCheck().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102015-003");

    }

    /**
     * SSMN0102 016 CHECK called without BEGIN (Token error since token not present)
     */
    @Test
    public void testSSMN0102016() {

        // アサート:CHECKのついているメソッドに対するリクエストの呼び出しでトークンが渡されていないのでtoken errorが発生する
        open(applicationContextUrl + "transactiontoken", IndexPage.class)
                .clickLink2().clickBtnFlow8().getH().shouldHave(exactText(
                        "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0102016-001");
    }

    /**
     * SSMN0103 001 同じセッションでNameSpaceごとに保持できるTransactionTokenの数 (デフォルト10個)が超えると自動で過去のトランザクショントークンを削除される (windowをMax+1起動)
     */
    @Test
    public void testSSMN0103001() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // 1回目のタブの状態を保存する
        String mainWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // 最初と同じ操作を10回都度新規タブを開き繰り返す
        for (int i = 0; i < 10; i++) {
            IndexPage indexPage = flow1Step2Page.clickOpenNewWindow();
            WebDriverRunner.getWebDriver().switchTo().window(
                    new LinkedList<String>(WebDriverRunner.getWebDriver()
                            .getWindowHandles()).getLast());
            indexPage.clickLink2().clickBtnFlow1();
        }

        // アサート:1回目のタブに移動してサブミットするとトークンエラーになっていること
        WebDriverRunner.getWebDriver().switchTo().window(mainWindow);
        flow1Step2Page.clickBtnIn().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103001-001");
    }

    /**
     * SSMN0103 002 同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される (カスタム設定 = 2)
     */
    @Test
    public void testSSMN0103002() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        CustomStoreSizeNextPage customStoreSizeNextPage = open(
                applicationContextUrl + "transactiontoken", IndexPage.class)
                        .clickLink3().clickBtnBegin1Other();

        // 1回目のタブの状態を保存する
        String mainWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // 新規タブを開く
        IndexPage indexPage = customStoreSizeNextPage.clickOpenNewWindow();
        WebDriverRunner.getWebDriver().switchTo().window(
                new LinkedList<String>(WebDriverRunner.getWebDriver()
                        .getWindowHandles()).getLast());

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        customStoreSizeNextPage = indexPage.clickLink3().clickBtnBegin1();

        // 2回目のタブの状態を保存する
        String conflictWindow = WebDriverRunner.getWebDriver()
                .getWindowHandle();

        // 保持できるTransactionTokenの数が超えるよう業務処理を繰り返す(2回)
        for (int i = 0; i < 2; i++) {
            indexPage = customStoreSizeNextPage.clickOpenNewWindow();
            WebDriverRunner.getWebDriver().switchTo().window(
                    new LinkedList<String>(WebDriverRunner.getWebDriver()
                            .getWindowHandles()).getLast());
            indexPage.clickLink3().clickBtnBegin1();
        }

        // アサート:1回目のタブに移動してサブミットするとトークンエラーになっていること
        WebDriverRunner.getWebDriver().switchTo().window(mainWindow);
        customStoreSizeNextPage.clickBtnIn1Other().getH().shouldNotHave(
                exactText("Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103002-001");

        // アサート:2回目のタブに移動してサブミットするとトークンエラーになっていること
        WebDriverRunner.getWebDriver().switchTo().window(conflictWindow);
        customStoreSizeNextPage.clickBtnIn1().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103002-002");
    }

    /**
     * SSMN0103 003 同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される (カスタム設定 = 2)
     */
    @Test
    public void testSSMN0103003() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        CustomStoreSizeNextPage customStoreSizeNextPage = open(
                applicationContextUrl + "transactiontoken", IndexPage.class)
                        .clickLink3().clickBtnBegin2();

        // 1回目のタブの状態を保存する
        String mainWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // 保持できるTransactionTokenの数が超えるよう業務処理を繰り返す(2回)
        for (int i = 0; i < 2; i++) {
            IndexPage indexPage = customStoreSizeNextPage.clickOpenNewWindow();
            WebDriverRunner.getWebDriver().switchTo().window(
                    new LinkedList<String>(WebDriverRunner.getWebDriver()
                            .getWindowHandles()).getLast());
            indexPage.clickLink3().clickBtnBegin2();
        }

        // アサート:1回目のタブに移動してサブミットするとトークンエラーになっていること
        WebDriverRunner.getWebDriver().switchTo().window(mainWindow);
        customStoreSizeNextPage.clickBtnIn2().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103003-001");
    }

    /**
     * SSMN0103 004 同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される (カスタム設定 = 1)
     */
    @Test
    public void testSSMN0103004() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        CustomStoreSizeNextPage customStoreSizeNextPage = open(
                applicationContextUrl + "transactiontoken", IndexPage.class)
                        .clickLink3().clickBtnBegin3();

        // 1回目のタブの状態を保存する
        String mainWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // 新規のタブで業務処理を行う
        IndexPage indexPage = customStoreSizeNextPage.clickOpenNewWindow();
        WebDriverRunner.getWebDriver().switchTo().window(
                new LinkedList<String>(WebDriverRunner.getWebDriver()
                        .getWindowHandles()).getLast());
        indexPage.clickLink3().clickBtnBegin3();

        // アサート:1回目のタブに移動してサブミットするとトークンエラーになっていること
        WebDriverRunner.getWebDriver().switchTo().window(mainWindow);
        customStoreSizeNextPage.clickBtnIn3().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103004-001");
    }

    /**
     * SSMN0103 005 同じセッションでNameSpaceごとに保持できるTransactionTokenの数 (デフォルト10個)と同じ場合、自動で過去のトランザクショントークンを削除されないこと
     */
    @Test
    public void testSSMN0103005() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // 1回目のタブの状態を保存する
        String mainWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // 保持できるTransactionTokenの数と同じ回数(1回目を含む)業務処理を繰り返す
        for (int i = 0; i < 9; i++) {
            IndexPage indexPage = flow1Step2Page.clickOpenNewWindow();
            WebDriverRunner.getWebDriver().switchTo().window(
                    new LinkedList<String>(WebDriverRunner.getWebDriver()
                            .getWindowHandles()).getLast());
            indexPage.clickLink2().clickBtnFlow1();
        }

        // アサート:1回目のタブに移動してサブミットしても正常にレスポンスが返却されること
        WebDriverRunner.getWebDriver().switchTo().window(mainWindow);
        flow1Step2Page.clickBtnIn().getResult().shouldHave(matchText(
                "transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103005-001");
    }

    /**
     * SSMN0103 006 同じセッションでNameSpaceごとに保持できるTransactionTokenの数
     * (デフォルト10個)が超えると自動で過去のトランザクショントークンを削除される(windowをMax起動し、上書きしたセッションでリクエスト送信)
     */
    @Test
    public void testSSMN0103006() {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // 1回目のタブの状態を保存する
        String mainWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // 保持できるTransactionTokenの数と同じ回数(1回目を含む)業務処理を繰り返す
        for (int i = 0; i < 9; i++) {
            IndexPage indexPage = flow1Step2Page.clickOpenNewWindow();
            WebDriverRunner.getWebDriver().switchTo().window(
                    new LinkedList<String>(WebDriverRunner.getWebDriver()
                            .getWindowHandles()).getLast());
            indexPage.clickLink2().clickBtnFlow1();
        }

        // 5回目のタブに移動する
        WebDriverRunner.getWebDriver().switchTo().window(
                new LinkedList<String>(WebDriverRunner.getWebDriver()
                        .getWindowHandles()).get(4));

        // アサート:5回目のタブから新規トークンが生成されても正常にレスポンスが返却されること
        flow1Step2Page.clickRedo1().clickBtnFlow1().getResult().shouldHave(
                matchText("transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103006-001");

        // アサート:1回目のタブに移動してサブミットするとトークンエラーになっていること
        WebDriverRunner.getWebDriver().switchTo().window(mainWindow);
        flow1Step2Page.clickBtnIn().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0103006-002");
    }

    /**
     * SSMN0104 001 <form:form>タグを使用しない場合は、<t:transaction /> を明示的に使用することにより、同じようにTransactionTokenCheckのhidden項目が埋め込まれる
     */
    @Test
    public void testSSMN0104001() {

        // アサート:Transaction トークンが生成されて出力ビューとして上記のJSPが指定されているときレスポンスにhidden項目「_TRANSACTION_TOKEN」が入っていること
        // 生成したトークンは「globalToken~xxx~xxx」の形であること
        open(applicationContextUrl + "transactiontoken", IndexPage.class)
                .clickLink2().clickBtnFlow6().getResult().shouldHave(matchText(
                        "globalToken~[0-9a-z]{32}~[0-9a-z]{32}"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0104001-001");
    }

    /**
     * SSMN0105 001 セッションタイムアウト時にDBに保持しているTransactionTokenが削除される
     * @throws InterruptedException
     */
    @Test
    public void testSSMN0105001() throws InterruptedException {

        // テスト実行(BEGINのついているメソッドに対するリクエスト)
        Flow1Step2Page flow1Step2Page = open(applicationContextUrl
                + "transactiontoken", IndexPage.class).clickLink2()
                        .clickBtnFlow1();

        // アサート:hidden項目「_TRANSACTION_TOKEN」が入っていること（作成されている）
        flow1Step2Page.getTransactionToken().shouldNotHave(empty);

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0105001-001");

        // サスペンド:APサーバのセッションタイムアウト設定時間経過待ち
        Thread.sleep((long) untilSessionTimeout * 1000);

        // アサート:INのついているメソッドに対するリクエストを行い、トークンエラーとなること
        flow1Step2Page.clickBtnIn().getH().shouldHave(exactText(
                "Transaction Token Error"));

        // 証跡取得
        screenshot("S02TransactionTokenTest-testSSMN0105001-002");
    }

}
