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

import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement h;

    private SelenideElement memberRandomNo;

    /**
     * Homeページのコンストラクタ。
     */
    public HomePage() {
        this.h = $("h1");
        this.memberRandomNo = $(byId("randomNo"));
    }

    /**
     * メンバーに割り振られるランダムな数字を取得する。
     * @return SelenideElement メンバーに割り振られるランダムな数字
     */
    public SelenideElement getMemberRandomNo() {
        return memberRandomNo;
    }

    /**
     * 見出しの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }

}
