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
package jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Transaction Token Function Testページ
 */
public class IndexPage {

    /**
     * 「@TransactionTokenCheck("NameSpace")」形式の確認用リンク
     * @return CreateInputPage
     */
    public CreateInputPage clickLink1() {
        $(byId("link1")).click();
        return page(CreateInputPage.class);
    }

    /**
     * TransactionTokenTypeを使ったTransactionTokenライフサイクル確認用リンク
     * @return FlowAllStep1Page
     */
    public FlowAllStep1Page clickLink2() {
        $(byId("link2")).click();
        return page(FlowAllStep1Page.class);
    }

    /**
     * TransactionTokenStoreのサイズチェック (同時処理数の一定数と同等、または超える時の動き)用リンク<br>
     * Custom Namespace用
     * @return CustomStoreSizeMenuPage
     */
    public CustomStoreSizeMenuPage clickLink3() {
        $(byId("link3")).click();
        return page(CustomStoreSizeMenuPage.class);
    }

    /**
     * TransactionTokenStoreのサイズチェック (同時処理数の一定数と同等、または超える時の動き)用リンク<br>
     * Custom Global Namespace用
     * @return CustomStoreSizeMenuPage
     */
    public CustomStoreSizeMenuPage clickLink4() {
        $(byId("link4")).click();
        return page(CustomStoreSizeMenuPage.class);
    }

    /**
     * TransactionTokenTypeを使ったTransactionTokenライフサイクル確認用リンク<br>
     * 「@TransactionTokenCheck(namespace = "NameSpace")」をクラスに付与しているケース
     * @return FlowAllNamespaceStep1Page
     */
    public FlowAllNamespaceStep1Page clickLink5() {
        $(byId("link5")).click();
        return page(FlowAllNamespaceStep1Page.class);
    }
}
