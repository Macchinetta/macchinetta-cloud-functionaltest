/*
 * Copyright 2014-2017 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.db;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.format.DateTimeFormat;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;

/**
 * アサートヘルパー
 *
 * @author NTT 電電太郎
 *
 */
public class AssertHelper {

    /**
     * DBの会員情報のアサート
     *
     * @throws SQLException
     *
     */
    public static void assertDBMember(Member expectedMember, ResultSet resultSet) throws SQLException {
        assertEquals(expectedMember.getCustomerNo(), resultSet.getString("customer_no"));
        assertEquals(expectedMember.getName(), resultSet.getString("name"));
        assertEquals(expectedMember.getFuriName(), resultSet.getString("furi_name"));
    }

    /**
     * JSPから取得する会員情報のアサート
     *
     * @throws SQLException
     *
     */
    public static void assertViewMember(Member expectedMember, HelloPage helloPage) {
        assertEquals(expectedMember.getCustomerNo(), helloPage.getCustomerNo().getText());
        assertEquals(expectedMember.getName(), helloPage.getName().getText());
        assertEquals(expectedMember.getFuriName(), helloPage.getFuriName().getText());
    }

    /**
     * DBの予約情報のアサート
     *
     * @param expectedReservation
     * @param resultSet
     * @throws SQLException
     */
    public static void assertDBReservation(Reservation expectedReservation, ResultSet resultSet) throws SQLException {
        assertEquals(expectedReservation.getReserveNo(), resultSet.getString("reserve_no"));
        assertEquals(DateTimeFormat.forPattern("yyyy-MM-dd").print(expectedReservation.getReserveDate()),
                resultSet.getDate("reserve_date").toString());
        assertEquals(expectedReservation.getTotalFare(), Integer.valueOf(resultSet.getInt("total_fare")));

        assertEquals(expectedReservation.getRepMember().getCustomerNo(), resultSet.getString("rep_customer_no"));
        assertEquals(expectedReservation.getRepMember().getName(), resultSet.getString("rep_name"));
        assertEquals(expectedReservation.getRepMember().getFuriName(), resultSet.getString("rep_furi_name"));
    }

    /**
     * JSPから取得する予約情報のアサート
     */
    public static void assertViewResvation(Reservation expectedReservation, HelloPage helloPage) {
        assertEquals(expectedReservation.getReserveNo(), helloPage.getReserveNo().getText());
        assertEquals(DateTimeFormat.forPattern("yyyy/MM/dd").print(expectedReservation.getReserveDate()),
                helloPage.getReserveDate().getText());
        assertEquals(expectedReservation.getTotalFare(),
                Integer.valueOf(helloPage.getTotalFare().getText()));

        assertEquals(expectedReservation.getRepMember().getCustomerNo(), helloPage.getRepCustomerNo().getText());
        assertEquals(expectedReservation.getRepMember().getName(), helloPage.getRepName().getText());
        assertEquals(expectedReservation.getRepMember().getFuriName(), helloPage.getRepFuriName().getText());
    }
}
