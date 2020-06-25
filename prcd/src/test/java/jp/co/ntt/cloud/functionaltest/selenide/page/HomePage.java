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

import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement cloudFrontResult;

    private SelenideElement reject;

    private SelenideElement appResult;

    private SelenideElement timer;

    /**
     * HomePageのコンストラクタ。
     */
    public HomePage() {
        this.timer = $(byId("timer"));
        this.cloudFrontResult = $(byId("cloudFrontResult"));
        this.reject = $(byId("reject"));
        this.appResult = $(byId("appResult"));
    }

    /**
     * ログアウトする。
     * @return LoginPage
     */
    public LoginPage logout() {
        $(byId("logout")).click();
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
     * 再取得をクリックする。
     * @return HomePage
     */
    public HomePage reload() {
        $(byId("reload")).click();
        return this;
    }

    /**
     * disableCookieをクリックする。
     * @return HomePage
     */
    public HomePage disableCookie() {
        $(byId("disableCookie")).click();
        return this;
    }

    /**
     * loadVerificationContentをクリックする。
     * @return HomePage
     */
    public HomePage loadVerificationContent() {
        $(byId("loadVerificationContent")).click();
        return this;
    }

    /**
     * 経過時間の要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getTimer() {
        return timer;
    }

    /**
     * CloudFront画像コンテンツ値の要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getCloudFrontResult() {
        return cloudFrontResult;
    }

    /**
     * アクセス拒否コンテンツの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getReject() {
        return reject;
    }

    /**
     * 比較画像コンテンツ値の要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getAppResult() {
        return appResult;
    }
}
