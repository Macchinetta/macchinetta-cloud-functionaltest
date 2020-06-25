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
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement h;

    private ElementsCollection p;

    /**
     * Homeページのコンストラクタ。
     */
    public HomePage() {
        this.h = $("h1");
        this.p = $$("p");
    }

    /**
     * inspectリンクをクリックする。
     * @return CaapPage
     */
    public CaapPage inspect() {
        $(byId("inspect")).click();
        return new CaapPage();
    }

    /**
     * ログアウトする。
     * @return LoginPage
     */
    public LoginPage logout() {
        $("button").click();
        return new LoginPage();
    }

    /**
     * h1要素のセレクタを返却する。
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }

    /**
     * ログインユーザ名が表示されるp要素のセレクタを返却する。
     * @return SelenideElement
     */
    public SelenideElement getAccountName() {
        return p.get(1);
    }

}
