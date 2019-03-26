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
 * 「@TransactionTokenCheck」のvalue(Namespace)指定確認用ページ
 */
public class CreateInputPage {

    /**
     * クラスのみに対して、@TransactionTokenCheck("transactiontoken")を指定している
     * @return CreateOutputPage
     */
    public CreateOutputPage clickBtn1() {
        $(byId("btn1")).click();
        return page(CreateOutputPage.class);
    }

    /**
     * クラスに対して、@TransactionTokenCheck("transactiontoken")とメソッドに、@TransactionTokenCheck("create")を指定している
     * @return CreateOutputPage
     */
    public CreateOutputPage clickBtn2() {
        $(byId("btn2")).click();
        return page(CreateOutputPage.class);
    }

    /**
     * メソッドのみに、@TransactionTokenCheck("create")を指定している
     * @return CreateOutputPage
     */
    public CreateOutputPage clickBtn3() {
        $(byId("btn3")).click();
        return page(CreateOutputPage.class);
    }

    /**
     * クラスとメソッド両方に対して、@TransactionTokenCheckのvalue指定はない<br>
     * つまり、TransactionTokenに"globalToken"(固定値)が適用される。
     * @return CreateOutputPage
     */
    public CreateOutputPage clickBtn4() {
        $(byId("btn4")).click();
        return page(CreateOutputPage.class);
    }

}
