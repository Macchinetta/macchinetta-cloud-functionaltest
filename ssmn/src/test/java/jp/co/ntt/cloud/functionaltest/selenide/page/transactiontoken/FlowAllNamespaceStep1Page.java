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

/**
 * TransactionTokenライフサイクル確認用ページ<br>
 * 「@TransactionTokenCheck(namespace = "NameSpace")」をクラスに付与しているケース
 */
public class FlowAllNamespaceStep1Page {

    /**
     * 「@TransactionTokenCheck(namespace = "NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.BEGIN)」が付与されたメソッドへのリクエスト
     * @return Flow1NamespaceStep2Page
     */
    public Flow1NamespaceStep2Page clickBtnFlow1() {
        $(byId("btn-flow1")).click();
        return page(Flow1NamespaceStep2Page.class);
    }

}
