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
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement notificationType;

    private SelenideElement headers;

    /**
     * HomePageのコンストラクタ。
     */
    public HomePage() {
        this.notificationType = $(byId("notificationType"));
        this.headers = $(byId("headers"));
    }

    /**
     * メールを送信する。
     * @param toAddress
     * @param type
     * @param body
     * @return HomePage
     */
    public HomePage send(String toAddress, String type, String body) {
        $(byName("to")).setValue(toAddress);
        $(byName("kind")).selectRadio(type);
        $(byName("body")).setValue(body);
        $(byId("sendMail")).click();
        return page(HomePage.class);
    }

    /**
     * 通知種別の要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getNotificationType() {
        return notificationType;
    }

    /**
     * ヘッダーの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getHeaders() {
        return headers;
    }
}
