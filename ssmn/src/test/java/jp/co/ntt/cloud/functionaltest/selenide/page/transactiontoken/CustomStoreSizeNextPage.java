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
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

import com.codeborne.selenide.SelenideElement;

/**
 * TransactionToken作成後に表示されるページ
 */
public class CustomStoreSizeNextPage {

    private SelenideElement h;

    /**
     * CustomStoreSizeNextのコンストラクタ
     */
    public CustomStoreSizeNextPage() {
        this.h = $("h2");
    }

    /**
     * 新規タブを開く
     * @return IndexPage
     */
    public IndexPage clickOpenNewWindow() {
        $(byId("open-new-window")).click();
        return page(IndexPage.class);
    }

    /**
     * 「@TransactionTokenCheck("transactiontoken/customTransactionStoreSize2")」がクラスに付与され、<br>
     * 「@TransactionTokenCheck(value = "createOther", type = TransactionTokenType.IN)」が付与されたメソッドへのリクエスト<br>
     * 保持できるTransactionTokenの上限を2個にカスタム設定している
     * @return CustomStoreSizeNextPage
     */
    public CustomStoreSizeNextPage clickBtnIn1Other() {
        $(byId("btn-in1-other")).click();
        return page(CustomStoreSizeNextPage.class);
    }

    /**
     * 「@TransactionTokenCheck("transactiontoken/customTransactionStoreSize2")」がクラスに付与され、<br>
     * 「@TransactionTokenCheck(value = "create", type = TransactionTokenType.IN)」が付与されたメソッドへのリクエスト<br>
     * 保持できるTransactionTokenの上限を2個にカスタム設定している
     * @return CustomStoreSizeNextPage
     */
    public CustomStoreSizeNextPage clickBtnIn1() {
        $(byId("btn-in1")).click();
        return page(CustomStoreSizeNextPage.class);
    }

    /**
     * クラスにTransactionTokenCheckの指定がなく、<br>
     * 「@TransactionTokenCheck(value = "create", type = TransactionTokenType.IN)」が付与されたメソッドへのリクエスト<br>
     * 保持できるTransactionTokenの上限を2個にカスタム設定している
     * @return CustomStoreSizeNextPage
     */
    public CustomStoreSizeNextPage clickBtnIn2() {
        $(byId("btn-in2")).click();
        return page(CustomStoreSizeNextPage.class);
    }

    /**
     * 「@TransactionTokenCheck(type = TransactionTokenType.IN)」(globalToken適用)が付与されたメソッドへのリクエスト<br>
     * 保持できるTransactionTokenの上限を1個にカスタム設定している
     * @return CustomStoreSizeNextPage
     */
    public CustomStoreSizeNextPage clickBtnIn3() {
        $(byId("btn-in3")).click();
        return page(CustomStoreSizeNextPage.class);
    }

    /**
     * h2要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }

}
