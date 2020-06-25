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
 * TransactionToken更新後に表示されるページ
 */
public class Flow2Step3Page {

    private SelenideElement transactionToken;

    private SelenideElement result;

    private SelenideElement h;

    /**
     * Flow2Step3Pageのコンストラクタ
     */
    public Flow2Step3Page() {
        this.transactionToken = $(byName("_TRANSACTION_TOKEN"));
        this.result = $(byId("result"));
        this.h = $("h2");
    }

    /**
     * 「@TransactionTokenCheck」のnamespaceがクラスとメソッドに指定されておらず(つまり、globalTokenの適用)、<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.CHECK)」が付与されたメソッドへのリクエスト
     * @return Flow2Step3Page
     */
    public Flow2Step3Page clickBtnCheck() {
        $(byId("btn-check")).click();
        return page(Flow2Step3Page.class);
    }

    /**
     * 「@TransactionTokenCheck」のnamespaceがクラスとメソッドに指定されておらず(つまり、globalTokenの適用)、<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.END)」が付与されたメソッドへのリクエスト
     * @return Flow2Step4Page
     */
    public Flow2Step4Page clickBtnEnd() {
        $(byId("btn-end")).click();
        return page(Flow2Step4Page.class);
    }

    /**
     * hidden項目のTransactionTokenの要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getTransactionToken() {
        return transactionToken;
    }

    /**
     * TransactionToken値表示用の要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getResult() {
        return result;
    }

    /**
     * h2要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }

}
