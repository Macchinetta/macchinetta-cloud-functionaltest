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
package jp.co.ntt.cloud.functionaltest.selenide.page.redissession;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.SelenideElement;

/**
 * Loginページ
 */
public class LoginPage {

    /**
     * "order/form"にアクセスした際のログイン処理。
     * @param userId 顧客番号
     * @param password パスワード
     * @return OrderFormPage
     */
    public OrderFormPage loginOrderForm(String userId, String password) {
        $(byId("userId")).setValue(userId);
        $(byId("password")).setValue(password);
        $$("tr").get(2).$("input").click();
        return new OrderFormPage();
    }

    /**
     * "session/isAuthenticated"にアクセスした際のログイン処理。
     * @param userId 顧客番号
     * @param password パスワード
     * @return IndexPage
     */
    public IndexPage loginIsAuthenticated(String userId, String password) {
        $(byId("userId")).setValue(userId);
        $(byId("password")).setValue(password);
        $$("tr").get(2).$("input").click();
        return new IndexPage();
    }

    /**
     * ログイン画面のメッセージ取得
     * @return SelenideElement
     */
    public SelenideElement getMessage() {
        return $(byId("title"));
    }
}
