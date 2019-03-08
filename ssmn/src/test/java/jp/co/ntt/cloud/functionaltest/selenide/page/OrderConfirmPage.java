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
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/**
 * 注文確認ページクラス
 */
public class OrderConfirmPage {

    /**
     * 注文確認の商品名を取得する。
     */
    public SelenideElement getCartItemName(int index) {
        return $(byId("confirm")).$(byId("itemName" + index));
    }

    /**
     * 注文確認の値段を取得する。
     */
    public SelenideElement getCartItemPrice(int index) {
        return $(byId("confirm")).$(byId("itemPrice" + index));
    }

    /**
     * 注文確認の値段を取得する。
     */
    public SelenideElement getCartItemQuantity(int index) {
        return $(byId("confirm")).$(byId("itemQuantity" + index));
    }

    /**
     * 注文するボタンを押す。
     */
    public OrderFinishPage orderFinish() {
        $(byId("viewOrderForm")).click();
        return new OrderFinishPage();
    }
}
