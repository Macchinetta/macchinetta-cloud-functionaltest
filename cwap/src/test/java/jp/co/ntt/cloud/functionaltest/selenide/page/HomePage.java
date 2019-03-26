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
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement h;

    private SelenideElement counter;

    private SelenideElement csrf;

    private SelenideElement transactionToken;

    /**
     * Homeページのコンストラクタ。
     */
    public HomePage() {
        this.h = $("h1");
        this.counter = $(byId("counter"));
        this.csrf = $(byName("_csrf"));
        this.transactionToken = $(byName("_TRANSACTION_TOKEN"));
    }

    /**
     * ログアウトする。
     */
    public void logout() {
        $(byId("logout")).click();
    }

    /**
     * transactionTokenCheckをクリックする。
     * @return TokenCheckPage
     */
    public TokenCheckPage confirmToken() {
        $(byId("transactionTokenCheck")).click();
        return new TokenCheckPage();
    }

    /**
     * loggingをクリックする。
     * @return LoggingPage
     */
    public LoggingPage logging() {
        $(byId("logging")).click();
        return new LoggingPage();
    }

    /**
     * showCustomViewをクリックする。
     * @return ShowCustomViewPage
     */
    public ShowCustomViewPage showCustomView() {
        $(byId("showCustomView")).click();
        return new ShowCustomViewPage();
    }

    /**
     * customErrorをクリックする。
     * @return CwapCustomErrorPage
     */
    public CwapCustomErrorPage customError() {
        $(byId("customError")).click();
        return new CwapCustomErrorPage();
    }

    /**
     * 見出しの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }

    /**
     * カウンタの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getCounter() {
        return counter;
    }

    /**
     * CSRFの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getCsrf() {
        return csrf;
    }

    /**
     * トランザクショントークンの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getTransactionToken() {
        return transactionToken;
    }
}
