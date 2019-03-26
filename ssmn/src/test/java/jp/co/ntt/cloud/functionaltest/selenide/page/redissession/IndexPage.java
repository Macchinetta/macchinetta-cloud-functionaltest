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
 * 「session/isAuthenticated」にアクセスした際表示されるIndexページ
 */
public class IndexPage {

    private SelenideElement h2;

    /**
     * IndexPageのコンストラクタ
     */
    public IndexPage() {
        this.h2 = $("h2");
    }

    /**
     * セッションタイムアウト後にpostを実行するボタンをクリックする。
     * @return SessionInvalidErrorPage
     */
    public SessionInvalidErrorPage clickPostButtonAfterTimeout() {
        $(byId("sessionPost")).click();
        return new SessionInvalidErrorPage();
    }

    /**
     * セッションタイムアウト後にgetを実行するボタンをクリックする。
     * @return SessionInvalidErrorPage
     */
    public SessionInvalidErrorPage clickGetButtonAfterTimeout() {
        $(byId("sessionGet")).click();
        return new SessionInvalidErrorPage();
    }

    /**
     * h2要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getH2() {
        return this.h2;
    }
}
