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
package jp.co.ntt.cloud.functionaltest.infra.common;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;

import jp.co.ntt.cloud.functionaltest.app.reservation.Reservation1ArgController;
import jp.co.ntt.cloud.functionaltest.app.reservation.Reservation2ArgController;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.infra.common.constants.SqlConstants;
import jp.co.ntt.cloud.functionaltest.infra.rdb.RDBUtil;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;

/**
 * シャードのテストヘルパークラス。
 */
public class ShardDBAssertUtil {

    @Inject
    RDBUtil dbUtil;

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /**
     * シャード数
     */
    private static final int NUMBER_OF_SHARD = 2;

    /**
     * シャード1に予約番号が偶数の予約情報、シャード2に予約番号奇数の予約情報が登録されていること
     * @param serviceUrl
     *        {@link Reservation1ArgController}を使用するなら{@code "arg1"}を、{@link Reservation2ArgController}を使用するなら、{@code "arg2"}を指定する。
     * @param expectedReservations 予約情報の期待値
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void shardInsertTest(String serviceUrl,
            List<Reservation> expectedReservations) throws ClassNotFoundException, SQLException {

        // シャード1,2に接続して予約情報が登録されていることを確認する。
        try ( // @formatter:off
                Connection defaultConn = dbUtil.getDefaultConnection();
                Connection shard1Conn = dbUtil.getShard1Connection();
                Connection shard2Conn = dbUtil.getShard2Connection();
                Statement defaultStmt = defaultConn.createStatement();
                Statement shard1Stmt = shard1Conn.createStatement();
                Statement shard2Stmt = shard2Conn.createStatement();) {
            // @formatter:on

            defaultStmt.executeUpdate(SqlConstants.MEMBER.TRUNCATE);
            shard1Stmt.executeUpdate(SqlConstants.RESERVATION.TRUNCATE);
            shard2Stmt.executeUpdate(SqlConstants.RESERVATION.TRUNCATE);

            given().get("member/register");
            given().get("reserve/" + serviceUrl + "/register");

            try ( // @formatter:off
                    ResultSet shard1Rset = shard1Stmt.executeQuery(
                            SqlConstants.RESERVATION.SELECT_ALL);
                    ResultSet shard2Rset = shard2Stmt.executeQuery(
                            SqlConstants.RESERVATION.SELECT_ALL);) {
                // @formatter:on

                // shard1のアサート
                // 予約情報期待値リストのインデックス
                int expextedShard1Index = 0;

                // 期待値の予約情報の会員情報が偶数となっているリスト
                List<Reservation> expectedReservationsEven = new ArrayList<>();
                for (Reservation reservation : expectedReservations) {
                    if (Integer.parseInt(reservation.getRepMember()
                            .getCustomerNo()) % NUMBER_OF_SHARD == 0) {
                        expectedReservationsEven.add(reservation);
                    }
                }
                while (shard1Rset.next()) {

                    Reservation expected = expectedReservationsEven.get(
                            expextedShard1Index);

                    // アサート:偶数会員番号の予約者の予約情報を確認する。
                    DBAssertUtil.assertDBReservation(expected, shard1Rset);

                    if (expextedShard1Index < expectedReservationsEven.size()) {
                        expextedShard1Index++;
                    } else {

                        // reserveMembers.size() 件以上登録されているはおかしいので、失敗にする。
                        fail("Result sets exceeded the prescribed number of row.");
                        break;
                    }
                }
                if (expextedShard1Index == 0) {
                    fail("Shard1 result set row is 0.");
                }

                // shar2のアサート
                // 予約情報期待値リストのインデックス
                int expextedShard2Index = 0;

                // 期待値の予約情報の会員情報が奇数となっているリスト
                List<Reservation> expectedReservationsOdd = new ArrayList<>();
                for (Reservation reservation : expectedReservations) {
                    if (Integer.parseInt(reservation.getRepMember()
                            .getCustomerNo()) % NUMBER_OF_SHARD == 1) {
                        expectedReservationsOdd.add(reservation);
                    }
                }
                while (shard2Rset.next()) {

                    Reservation expected = expectedReservationsOdd.get(
                            expextedShard2Index);

                    // アサート:奇数会員番号の予約者の予約情報を確認する。
                    DBAssertUtil.assertDBReservation(expected, shard2Rset);

                    if (expextedShard2Index < expectedReservationsOdd.size()) {
                        expextedShard2Index++;
                    } else {

                        // reserveMembers.size() 件以上登録されているはおかしいので、失敗にする。
                        fail("Result sets exceeded the prescribed number of row.");
                    }
                }
                if (expextedShard2Index == 0) {
                    fail("Shard2 result set row is 0.");
                }
            }
        }
    }

    /**
     * シャード1,2から予約1件取得できること
     * @param serviceUrl
     *        {@link Reservation1ArgController}を使用するなら{@code "arg1"}を、{@link Reservation2ArgController}を使用するなら、{@code "arg2"}を指定する。
     * @param expectedReservations 予約情報の期待値
     */
    public void shardSelectTest(String serviceUrl,
            List<Reservation> expectedReservations) {

        // 取得する予約情報(shard1から)
        Reservation expected = expectedReservations.get(0);

        HomePage homePage = open(applicationContextUrl + "reserve/" + serviceUrl
                + "/get?customerNo=" + expected.getRepMember().getCustomerNo()
                + "&reserveNo=" + expected.getReserveNo(), HomePage.class);

        // アサート:偶数会員番号の予約者の予約情報を確認する。
        DBAssertUtil.assertViewResvation(expected, homePage);

        // 証跡取得
        screenshot("shard1DB" + serviceUrl + "SelectTest");

        // 取得する予約情報(shard2から)
        expected = expectedReservations.get(1);

        homePage = open(applicationContextUrl + "reserve/" + serviceUrl
                + "/get?customerNo=" + expected.getRepMember().getCustomerNo()
                + "&reserveNo=" + expected.getReserveNo(), HomePage.class);

        // アサート:奇数会員番号の予約者の予約情報を確認する。
        DBAssertUtil.assertViewResvation(expected, homePage);

        // 証跡取得
        screenshot("shard2DB" + serviceUrl + "SelectTest");
    }

    /**
     * シャード1,2から1件の予約情報を更新(旅行代金を0にする)できること
     * @param serviceUrl
     *        {@link Reservation1ArgController}を使用するなら{@code "arg1"}を、{@link Reservation2ArgController}を使用するなら、{@code "arg2"}を指定する。
     * @param expectedReservations 予約情報の期待値
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void shardUpdateTest(String serviceUrl,
            List<Reservation> expectedReservations) throws ClassNotFoundException, SQLException {

        // 取得する予約情報の会員番号
        Reservation shard1Expected = expectedReservations.get(0);
        Reservation shard2Expected = expectedReservations.get(1);

        shard1Expected.setTotalFare(0);
        shard2Expected.setTotalFare(0);

        given().get("reserve/" + serviceUrl + "/update?customerNo="
                + shard1Expected.getRepMember().getCustomerNo() + "&reserveNo="
                + shard1Expected.getReserveNo());
        given().get("reserve/" + serviceUrl + "/update?customerNo="
                + shard2Expected.getRepMember().getCustomerNo() + "&reserveNo="
                + shard2Expected.getReserveNo());

        // Default DBに接続して予約情報が更新されていることを確認する。
        try ( // @formatter:off
                Connection shard1Conn = dbUtil.getShard1Connection();
                Connection shard2Conn = dbUtil.getShard2Connection();
                PreparedStatement shard1Pstmt = shard1Conn.prepareStatement(
                        SqlConstants.RESERVATION.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                PreparedStatement shard2Pstmt = shard2Conn.prepareStatement(
                        SqlConstants.RESERVATION.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);) {
            // @formatter:on

            shard1Pstmt.setString(1, shard1Expected.getReserveNo());
            shard2Pstmt.setString(1, shard2Expected.getReserveNo());

            try ( // @formatter:off
                    ResultSet shard1Rset = shard1Pstmt.executeQuery();
                    ResultSet shard2Rset = shard2Pstmt.executeQuery();) {
                // @formatter:on

                // shard1のアサート
                while (shard1Rset.next()) {

                    // アサート:偶数会員番号の予約者の予約情報を確認する。
                    DBAssertUtil.assertDBReservation(shard1Expected,
                            shard1Rset);
                }

                // アサート:取得件数が0件でないことを確認する。
                shard1Rset.last();
                assertNotEquals(0, shard1Rset.getRow());

                // shard2のアサート
                while (shard2Rset.next()) {

                    // アサート:奇数会員番号の予約者の予約情報を確認する。
                    DBAssertUtil.assertDBReservation(shard2Expected,
                            shard2Rset);
                }

                // アサート:取得件数が0件でないことを確認する。
                shard1Rset.last();
                assertNotEquals(0, shard1Rset.getRow());
            }
        }
    }

    /**
     * シャード1,2から1件の予約情報を削除できること
     * @param serviceUrl
     *        {@link Reservation1ArgController}を使用するなら{@code "arg1"}を、{@link Reservation2ArgController}を使用するなら、{@code "arg2"}を指定する。
     * @param expectedReservations 予約情報の期待値
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void shardDeleteTest(String serviceUrl,
            List<Reservation> expectedReservations) throws ClassNotFoundException, SQLException {

        // 取得する予約情報の会員番号
        Reservation shard1Expected = expectedReservations.get(0);
        Reservation shard2Expected = expectedReservations.get(1);

        given().get("reserve/" + serviceUrl + "/delete?customerNo="
                + shard1Expected.getRepMember().getCustomerNo() + "&reserveNo="
                + shard1Expected.getReserveNo());
        given().get("reserve/" + serviceUrl + "/delete?customerNo="
                + shard2Expected.getRepMember().getCustomerNo() + "&reserveNo="
                + shard2Expected.getReserveNo());

        // Default DBに接続して予約情報が削除されていることを確認する。
        try ( // @formatter:off
                Connection shard1Conn = dbUtil.getShard1Connection();
                Connection shard2Conn = dbUtil.getShard2Connection();
                PreparedStatement shard1Pstmt = shard1Conn.prepareStatement(
                        SqlConstants.RESERVATION.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                PreparedStatement shard2Pstmt = shard2Conn.prepareStatement(
                        SqlConstants.RESERVATION.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);) {
            // @formatter:on

            shard1Pstmt.setString(1, shard1Expected.getReserveNo());
            shard2Pstmt.setString(1, shard2Expected.getReserveNo());

            try ( // @formatter:off
                    ResultSet shard1Rset = shard1Pstmt.executeQuery();
                    ResultSet shard2Rset = shard2Pstmt.executeQuery();) {
                // @formatter:on

                // アサート:取得件数が0件であることを確認する。(shard1)
                shard1Rset.last();
                assertEquals(0, shard1Rset.getRow());

                // アサート:取得件数が0件であることを確認する。(shard2)
                shard2Rset.last();
                assertEquals(0, shard2Rset.getRow());
            }
        }
    }
}
