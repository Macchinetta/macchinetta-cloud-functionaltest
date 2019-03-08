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

import com.codeborne.selenide.SelenideElement;

/*
 * Helloページのページオブジェクトクラス。
 */
public class HelloPage {

    /**
     * ログアウトする。
     * @return TopPage トップページ
     */
    public TopPage logout() {
        $("button").click();
        return new TopPage();
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
     * 予約番号を返却する。
     * @return SelenideElement 予約番号
     */
    public SelenideElement getReserveNo() {
        return $(byId("reserveNo"));
    }

    /**
     * 予約日を返却する。
     * @return SelenideElement 予約日
     */
    public SelenideElement getReserveDate() {
        return $(byId("reserveDate"));
    }

    /**
     * 合計金額を返却する。
     * @return SelenideElement 合計金額
     */
    public SelenideElement getTotalFare() {
        return $(byId("totalFare"));
    }

    /**
     * 予約代表者会員番号を返却する。
     * @return SelenideElement 予約代表者会員番号
     */
    public SelenideElement getRepCustomerNo() {
        return $(byId("repCustomerNo"));
    }

    /**
     * 予約代表者会員氏名を返却する。
     * @return SelenideElement 予約代表者会員氏名
     */
    public SelenideElement getRepName() {
        return $(byId("repName"));
    }

    /**
     * 予約代表者会員氏名(ふりがな)を返却する。
     * @return SelenideElement 予約代表者会員氏名(ふりがな)
     */
    public SelenideElement getRepFuriName() {
        return $(byId("repFuriName"));
    }

    /**
     * 会員番号を返却する。
     * @return SelenideElement 会員番号
     */
    public SelenideElement getCustomerNo() {
        return $(byId("customerNo"));
    }

    /**
     * 会員氏名を返却する。
     * @return SelenideElement 会員氏名
     */
    public SelenideElement getName() {
        return $(byId("name"));
    }

    /**
     * 会員氏名(ふりがな)を返却する。
     * @return SelenideElement 会員氏名(ふりがな)
     */
    public SelenideElement getFuriName() {
        return $(byId("furiName"));
    }

}
