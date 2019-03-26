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
package jp.co.ntt.cloud.functionaltest.selenide.common;

import static com.codeborne.selenide.Condition.exactText;
import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.format.DateTimeFormat;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;

/**
 * アサートヘルパークラス。
 */
public class DBAssertUtil {

    /**
     * DBの会員情報をアサートする。
     * @param expectedMember
     * @param resultSet
     * @throws SQLException
     */
    public static void assertDBMember(Member expectedMember,
            ResultSet resultSet) throws SQLException {

        // アサート:期待される会員番号と一致すること。
        assertEquals(expectedMember.getCustomerNo(), resultSet.getString(
                "customer_no"));

        // アサート:期待される予約者名と一致すること。
        assertEquals(expectedMember.getName(), resultSet.getString("name"));

        // アサート:期待される予約者名（かな）と一致すること。
        assertEquals(expectedMember.getFuriName(), resultSet.getString(
                "furi_name"));
    }

    /**
     * JSPから取得する会員情報をアサートする。
     * @param expectedMember
     * @param homePage
     */
    public static void assertViewMember(Member expectedMember,
            HomePage homePage) {

        // アサート:期待される会員番号と一致すること。
        homePage.getCustomerNo().shouldHave(exactText(expectedMember
                .getCustomerNo()));

        // アサート:期待される予約者名と一致すること。
        homePage.getName().shouldHave(exactText(expectedMember.getName()));

        // アサート:期待される予約者名（かな）と一致すること。
        homePage.getFuriName().shouldHave(exactText(expectedMember
                .getFuriName()));
    }

    /**
     * DBの予約情報をアサートする。
     * @param expectedReservation
     * @param resultSet
     * @throws SQLException
     */
    public static void assertDBReservation(Reservation expectedReservation,
            ResultSet resultSet) throws SQLException {

        // アサート:期待される予約番号と一致すること。
        assertEquals(expectedReservation.getReserveNo(), resultSet.getString(
                "reserve_no"));

        // アサート:期待される予約日と一致すること。
        assertEquals(DateTimeFormat.forPattern("yyyy-MM-dd").print(
                expectedReservation.getReserveDate()), resultSet.getDate(
                        "reserve_date").toString());

        // アサート:期待される合計金額と一致すること。
        assertEquals(expectedReservation.getTotalFare(), Integer.valueOf(
                resultSet.getInt("total_fare")));

        // アサート:期待される会員番号と一致すること。
        assertEquals(expectedReservation.getRepMember().getCustomerNo(),
                resultSet.getString("rep_customer_no"));

        // アサート:期待される予約者名と一致すること。
        assertEquals(expectedReservation.getRepMember().getName(), resultSet
                .getString("rep_name"));

        // アサート:期待される予約者名（かな）と一致すること。
        assertEquals(expectedReservation.getRepMember().getFuriName(), resultSet
                .getString("rep_furi_name"));
    }

    /**
     * JSPから取得する予約情報をアサートする。
     * @param expectedReservation
     * @param homePage
     */
    public static void assertViewResvation(Reservation expectedReservation,
            HomePage homePage) {

        // アサート:期待される予約番号と一致すること。
        homePage.getReserveNo().shouldHave(exactText(expectedReservation
                .getReserveNo()));

        // アサート:期待される予約日と一致すること。
        homePage.getReserveDate().shouldHave(exactText(DateTimeFormat
                .forPattern("yyyy/MM/dd").print(expectedReservation
                        .getReserveDate())));

        // アサート:期待される合計金額と一致すること。
        homePage.getTotalFare().shouldHave(exactText(expectedReservation
                .getTotalFare().toString()));

        // アサート:期待される会員番号と一致すること。
        homePage.getRepCustomerNo().shouldHave(exactText(expectedReservation
                .getRepMember().getCustomerNo()));

        // アサート:期待される予約者名と一致すること。
        homePage.getRepName().shouldHave(exactText(expectedReservation
                .getRepMember().getName()));

        // アサート:期待される予約者名（かな）と一致すること。
        homePage.getRepFuriName().shouldHave(exactText(expectedReservation
                .getRepMember().getFuriName()));
    }
}
