/*
 * Copyright(c) 2017 NTT Corporation.
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
 */
package jp.co.ntt.cloud.functionaltest.domain.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

/**
 * 予約情報。
 * @author NTT 電電太郎
 */
public class Reservation implements Serializable {

    /**
     * シリアルバージョンUID。
     */
    private static final long serialVersionUID = -990618416759771212L;

    /**
     * 予約番号。
     */
    private String reserveNo;

    /**
     * 予約日付。
     */
    private DateTime reserveDate;

    /**
     * 合計金額。
     */
    private Integer totalFare;

    /**
     * 予約代表者の会員情報。
     */
    private Member repMember;

    /**
     * 予約番号を取得する。
     * @return 予約番号
     */
    public String getReserveNo() {
        return reserveNo;
    }

    /**
     * 予約番号を設定する。
     * @param reserveNo 予約番号
     */
    public void setReserveNo(String reserveNo) {
        this.reserveNo = reserveNo;
    }

    /**
     * 予約日付を取得する。
     * @return 予約日付
     */
    public DateTime getReserveDate() {
        return reserveDate;
    }

    /**
     * 予約日付を設定する。
     * @param dateTime 予約日付
     */
    public void setReserveDate(DateTime dateTime) {
        this.reserveDate = dateTime;
    }

    /**
     * 合計金額を取得する。
     * @return 合計金額
     */
    public Integer getTotalFare() {
        return totalFare;
    }

    /**
     * 合計金額を設定する。
     * @param totalFare 合計金額
     */
    public void setTotalFare(Integer totalFare) {
        this.totalFare = totalFare;
    }

    /**
     * 予約代表者の会員情報を取得する。
     * @return 予約代表者の会員情報
     */
    public Member getRepMember() {
        return repMember;
    }

    /**
     * 予約代表者の会員情報を設定する。
     * @param repMember 予約代表者の会員情報
     */
    public void setRepMember(Member repMember) {
        this.repMember = repMember;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}
