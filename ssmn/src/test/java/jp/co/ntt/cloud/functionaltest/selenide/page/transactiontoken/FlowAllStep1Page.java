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
package jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

import com.codeborne.selenide.SelenideElement;

/**
 * TransactionTokenライフサイクル確認用ページ
 */
public class FlowAllStep1Page {

    private SelenideElement transactionToken;

    /**
     * FlowAllStep1Pageのコンストラクタ
     */
    public FlowAllStep1Page() {
        this.transactionToken = $(byName("_TRANSACTION_TOKEN"));
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」が付与されたメソッドへのリクエスト
     * @return Flow1Step2Page
     */
    public Flow1Step2Page clickBtnFlow1() {
        $(byId("btn-flow1")).click();
        return page(Flow1Step2Page.class);
    }

    /**
     * 「@TransactionTokenCheck」のnamespaceがクラスとメソッドに指定されておらず(つまり、globalTokenの適用)、<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」が付与されたメソッドへのリクエスト
     * @return Flow2Step2Page
     */
    public Flow2Step2Page clickBtnFlow2() {
        $(byId("btn-flow2")).click();
        return page(Flow2Step2Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.IN)」が付与されたメソッドへのリクエスト
     * @return Flow1Step3Page
     */
    public Flow1Step3Page clickBtnFlow3() {
        $(byId("btn-flow3")).click();
        return page(Flow1Step3Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.END)」が付与されたメソッドへのリクエスト
     * @return Flow1Step4Page
     */
    public Flow1Step4Page clickBtnFlow4() {
        $(byId("btn-flow4")).click();
        return page(Flow1Step4Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」が付与されたメソッドへのリクエスト<br>
     * 擬似的な入力チェックエラーが発生し、画面遷移は行われない。
     * @return FlowAllStep1Page
     */
    public FlowAllStep1Page clickBtnFlow5_1() {
        $(byId("btn-flow5_1")).click();
        return page(FlowAllStep1Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」が付与されたメソッドへのリクエスト
     * @return Flow1Step2Page
     */
    public Flow1Step2Page clickBtnFlow5_2() {
        $(byId("btn-flow5_2")).click();
        return page(Flow1Step2Page.class);
    }

    /**
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」(globalToken適用)が付与されたメソッドへのリクエスト
     * @return TransactionTokenDisplayStartPage
     */
    public TransactionTokenDisplayStartPage clickBtnFlow6() {
        $(byId("btn-flow6")).click();
        return page(TransactionTokenDisplayStartPage.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」が付与されたメソッドへのリクエスト
     * @return Flow1Step2ToDifferentNamespacePage
     */
    public Flow1Step2ToDifferentNamespacePage clickBtnFlow7() {
        $(byId("btn-flow7")).click();
        return page(Flow1Step2ToDifferentNamespacePage.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.CHECK)」が付与されたメソッドへのリクエスト
     * @return Flow1Step3Page
     */
    public Flow1Step3Page clickBtnFlow8() {
        $(byId("btn-flow8")).click();
        return page(Flow1Step3Page.class);
    }

    /**
     * hidden項目のTransactionTokenの要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getTransactionToken() {
        return transactionToken;
    }

}
