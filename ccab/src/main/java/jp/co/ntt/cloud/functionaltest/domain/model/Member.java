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
package jp.co.ntt.cloud.functionaltest.domain.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * カード会員情報。
 * @author NTT 電電太郎
 */
public class Member implements Serializable {

    /**
     * シリアルバージョンUID。
     */
    private static final long serialVersionUID = 39772735547265219L;

    /**
     * お客様番号。
     */
    private String customerNo;

    /**
     * 名前。
     */
    private String name;

    /**
     * フリガナ。
     */
    private String furiName;

    /**
     * randomな数字(Cacheのテスト用)
     */
    private Integer randomNo;

    /**
     * お客様番号を取得する。
     * @return お客様番号
     */
    public String getCustomerNo() {
        return customerNo;
    }

    /**
     * お客様番号を設定する。
     * @param customerNo お客様番号
     */
    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    /**
     * 名前を取得する。
     * @return 名前
     */
    public String getName() {
        return name;
    }

    /**
     * 名前を設定する。
     * @param name 名前
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * フリガナ名を取得する。
     * @return フリガナ名。
     */
    public String getFuriName() {
        return furiName;
    }

    /**
     * フリガナ名を設定する。
     * @param kanaName フリガナ名
     */
    public void setFuriName(String furiName) {
        this.furiName = furiName;
    }

    /**
     * ランダムな数字を取得する。
     * @return ランダムな数字。
     */
    public Integer getRandomNo() {
        return randomNo;
    }

    /**
     * ランダムな数字を設定する。
     * @param randomNo ランダムな数字
     */
    public void setRandomNo(Integer randomNo) {
        this.randomNo = randomNo;
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
