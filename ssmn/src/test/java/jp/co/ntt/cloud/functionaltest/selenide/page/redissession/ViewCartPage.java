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
package jp.co.ntt.cloud.functionaltest.selenide.page.redissession;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/**
 * ViewCartページ
 */
public class ViewCartPage {

    /**
     * カートの中の商品名を取得する。
     * @param index
     * @return SelenideElement
     */
    public SelenideElement getCartItemName(int index) {
        return $(byId("cart")).$(byId("itemName" + index));
    }

    /**
     * カートの中の値段を取得する。
     * @param index
     * @return SelenideElement
     */
    public SelenideElement getCartItemPrice(int index) {
        return $(byId("cart")).$(byId("itemPrice" + index));
    }

    /**
     * セッションIDを取得する
     * @return SelenideElement
     */
    public SelenideElement getSessionId() {
        return $(byId("sessionId"));
    }

    /**
     * 注文内容確認ボタンを押す
     * @return OrderConfirmPage
     */
    public OrderConfirmPage orderConfirm() {
        $(byId("viewOrderConfirm")).click();
        return new OrderConfirmPage();
    }

    /**
     * 注文の個数を変更する
     * @param index カートに表示されているアイテムのインデックス(0からスタートする)。
     * @param quantity 個数
     * @return ViewCartPage
     */
    public ViewCartPage changeQuantity(Integer index, Integer quantity) {
        $(byId("itemQuantity" + index)).setValue(quantity.toString());
        $(byId("changeQuantity" + index)).click();
        return new ViewCartPage();
    }
}
