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
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private ElementsCollection p;

    private SelenideElement processtime;

    /**
     * HomePageのコンストラクタ。
     */
    public HomePage() {
        this.p = $$("p");
        this.processtime = $(byId("processtime"));
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
     * ログイン状態を返却する。
     * @return boolean ログイン状態
     */
    public boolean isLoggedIn() {
        if ($(byId("wrapper")).$(byText("Logout")).exists()) {
            return true;
        }
        return false;
    }

    /**
     * ログインユーザ名が表示されるp要素のセレクタを返却する。
     * @return SelenideElement
     */
    public SelenideElement getAccountName() {
        return p.get(1);
    }

    /**
     * processtimeが表示される要素のセレクタを返却する。
     * @return SelenideElement
     */
    public SelenideElement getProcesstime() {
        return processtime;
    }
}
