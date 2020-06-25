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

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/**
 * TransactionToken削除後に表示されるページ
 */
public class Flow2Step4Page {

    private SelenideElement transactionToken;

    private SelenideElement h;

    /**
     * Flow2Step4Pageのコンストラクタ
     */
    public Flow2Step4Page() {
        this.transactionToken = $(byName("_TRANSACTION_TOKEN"));
        this.h = $("h2");
    }

    /**
     * hidden項目のTransactionTokenの要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getTransactionToken() {
        return transactionToken;
    }

    /**
     * h2要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }

}
