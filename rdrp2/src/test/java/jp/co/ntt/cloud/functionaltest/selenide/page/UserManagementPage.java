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
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

/**
 * UserManagementページ
 */
public class UserManagementPage {

    /**
     * 顧客再読み込み
     * @return UserManagementPage
     */
    public UserManagementPage reload() {
        $(byText("reload")).click();
        return this;
    }

    /**
     * 顧客登録
     * @param lastName 姓
     * @param firstName 名
     * @return UserManagementPage
     */
    public UserManagementPage register(String lastName, String firstName) {
        $(byId("inputLastName")).setValue(lastName);
        $(byId("inputFirstName")).setValue(firstName);
        $(byId("button_register")).click();
        return this;
    }

    /**
     * 指定行の姓が表示される要素を返却する。
     * @param rowNumber 行番号
     * @return SelenideElement
     */
    public SelenideElement getLastNameColumn(Integer rowNumber) {
        return $(byId("userListTable")).$$("tr").get(rowNumber).$(".lastName");
    }

    /**
     * 指定行の名が表示される要素を返却する。
     * @param rowNumber 行番号
     * @return SelenideElement
     */
    public SelenideElement getFirstNameColumn(Integer rowNumber) {
        return $(byId("userListTable")).$$("tr").get(rowNumber).$(".firstName");
    }

    /**
     * テーブル行のコレクション要素を返却する。
     * @return ElementsCollection
     */
    public ElementsCollection getRows() {
        return $(byId("userListTable")).$$("tr");
    }
}
