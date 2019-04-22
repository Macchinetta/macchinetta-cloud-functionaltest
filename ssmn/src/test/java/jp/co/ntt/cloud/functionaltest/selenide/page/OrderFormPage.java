/*
 * Copyright(c) 2017 NTT Corporation.
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
 */
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;

/**
 * 注文ページクラス
 */
public class OrderFormPage {

    /**
     * 注文IDが奇数のものだけ注文する
     */
    public void orderOddIdProduct() {
        $(byId("add1")).click();
        $(byId("add3")).click();
    }

    /**
     * カートの内容表示
     */
    public ViewCartPage viewCart() {
        $(byId("viewCart")).click();
        return new ViewCartPage();
    }
}
