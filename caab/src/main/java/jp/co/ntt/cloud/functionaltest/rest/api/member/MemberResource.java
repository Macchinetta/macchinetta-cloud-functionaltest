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
 */
package jp.co.ntt.cloud.functionaltest.rest.api.member;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MemberResource implements Serializable {

    private static final long serialVersionUID = -2463489339066181241L;

    @NotNull
    @Size(min = 1, max = 10)
    private String kanjiFamilyName;

    @NotNull
    @Size(min = 1, max = 10)
    private String kanjiGivenName;

    @NotNull
    @Size(min = 1, max = 10)
    private String kanaFamilyName;

    @NotNull
    @Size(min = 1, max = 10)
    private String kanaGivenName;

    @NotNull
    @Min(1900)
    private Integer yearOfBirth;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer monthOfBirth;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer dayOfBirth;

    @NotNull
    @Size(min = 2, max = 5)
    private String tel1;

    @NotNull
    @Size(min = 1, max = 4)
    private String tel2;

    @NotNull
    private String tel3;

    @NotNull
    private String zipCode1;

    @NotNull
    private String zipCode2;

    @NotNull
    @Size(min = 1, max = 60)
    private String address;

    public String getKanjiFamilyName() {
        return kanjiFamilyName;
    }

    public void setKanjiFamilyName(String kanjiFamilyName) {
        this.kanjiFamilyName = kanjiFamilyName;
    }

    public String getKanjiGivenName() {
        return kanjiGivenName;
    }

    public void setKanjiGivenName(String kanjiGivenName) {
        this.kanjiGivenName = kanjiGivenName;
    }

    public String getKanaFamilyName() {
        return kanaFamilyName;
    }

    public void setKanaFamilyName(String kanaFamilyName) {
        this.kanaFamilyName = kanaFamilyName;
    }

    public String getKanaGivenName() {
        return kanaGivenName;
    }

    public void setKanaGivenName(String kanaGivenName) {
        this.kanaGivenName = kanaGivenName;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getMonthOfBirth() {
        return monthOfBirth;
    }

    public void setMonthOfBirth(Integer monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public Integer getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(Integer dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public String getTel1() {
        return tel1;
    }

    public void setTel1(String tel1) {
        this.tel1 = tel1;
    }

    public String getTel2() {
        return tel2;
    }

    public void setTel2(String tel2) {
        this.tel2 = tel2;
    }

    public String getTel3() {
        return tel3;
    }

    public void setTel3(String tel3) {
        this.tel3 = tel3;
    }

    public String getZipCode1() {
        return zipCode1;
    }

    public void setZipCode1(String zipCode1) {
        this.zipCode1 = zipCode1;
    }

    public String getZipCode2() {
        return zipCode2;
    }

    public void setZipCode2(String zipCode2) {
        this.zipCode2 = zipCode2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
