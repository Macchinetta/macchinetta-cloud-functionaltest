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

    private SelenideElement reserveNo;

    private SelenideElement reserveDate;

    private SelenideElement totalFare;

    private SelenideElement repCustomerNo;

    private SelenideElement repName;

    private SelenideElement repFuriName;

    private SelenideElement customerNo;

    private SelenideElement name;

    private SelenideElement furiName;

    /**
     * Homeページのコンストラクタ。
     */
    public HomePage() {
        this.reserveNo = $(byId("reserveNo"));
        this.reserveDate = $(byId("reserveDate"));
        this.totalFare = $(byId("totalFare"));
        this.repCustomerNo = $(byId("repCustomerNo"));
        this.repName = $(byId("repName"));
        this.repFuriName = $(byId("repFuriName"));
        this.customerNo = $(byId("customerNo"));
        this.name = $(byId("name"));
        this.furiName = $(byId("furiName"));
    }

    /**
     * 予約番号を返却する。
     * @return SelenideElement 予約番号
     */
    public SelenideElement getReserveNo() {
        return reserveNo;
    }

    /**
     * 予約日を返却する。
     * @return SelenideElement 予約日
     */
    public SelenideElement getReserveDate() {
        return reserveDate;
    }

    /**
     * 合計金額を返却する。
     * @return SelenideElement 合計金額
     */
    public SelenideElement getTotalFare() {
        return totalFare;
    }

    /**
     * 予約代表者会員番号を返却する。
     * @return SelenideElement 予約代表者会員番号
     */
    public SelenideElement getRepCustomerNo() {
        return repCustomerNo;
    }

    /**
     * 予約代表者会員氏名を返却する。
     * @return SelenideElement 予約代表者会員氏名
     */
    public SelenideElement getRepName() {
        return repName;
    }

    /**
     * 予約代表者会員氏名(ふりがな)を返却する。
     * @return SelenideElement 予約代表者会員氏名(ふりがな)
     */
    public SelenideElement getRepFuriName() {
        return repFuriName;
    }

    /**
     * 会員番号を返却する。
     * @return SelenideElement 会員番号
     */
    public SelenideElement getCustomerNo() {
        return customerNo;
    }

    /**
     * 会員氏名を返却する。
     * @return SelenideElement 会員氏名
     */
    public SelenideElement getName() {
        return name;
    }

    /**
     * 会員氏名(ふりがな)を返却する。
     * @return SelenideElement 会員氏名(ふりがな)
     */
    public SelenideElement getFuriName() {
        return furiName;
    }

}
