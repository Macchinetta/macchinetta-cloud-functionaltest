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
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import jp.co.ntt.cloud.functionaltest.db.DBUtil;
import jp.co.ntt.cloud.functionaltest.db.SQLConst;
import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.selenide.common.DBAssertUtil;
import jp.co.ntt.cloud.functionaltest.selenide.common.DBExpectedDataFactory;
import jp.co.ntt.cloud.functionaltest.selenide.common.ShardDBAssertUtil;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Inject
    private DBUtil dbUtil;

    @Inject
    private ShardDBAssertUtil shardDBAssertUtil;

    // 会員情報の期待値リスト
    private List<Member> expectedMembers = null;

    // 予約情報の期待値リスト
    private List<Reservation> expectedReservations = null;

    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // RestAssuredのベースURI設定
        RestAssured.baseURI = this.applicationContextUrl;

        // 会員情報の期待値リスト設定
        expectedMembers = DBExpectedDataFactory.expectedMembers();

        // 予約情報の期待値リスト
        expectedReservations = DBExpectedDataFactory.expectedReserves();
    }

    /**
     * DTSH0101 001 シャード1に予約番号が偶数の予約情報、シャード2に予約番号奇数の予約情報が登録されていることを確認する(Serviceの引数が1つの時)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0101_001() throws ClassNotFoundException, SQLException {
        shardDBAssertUtil.shardInsertTest("1arg", expectedReservations);
    }

    /**
     * DTSH0102 001 002 シャード1,2から予約1件取得できることを確認する(Serviceの引数が1つの時)
     */
    @Test
    public void testDTSH0102_001_002() {
        shardDBAssertUtil.shardSelectTest("1arg", expectedReservations);
    }

    /**
     * DTSH0103 001 002 シャード1,2から1件の予約情報を更新(旅行代金を0にする)できることを確認する(Serviceの引数が1つの時)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0103_001_002() throws ClassNotFoundException, SQLException {

        // 取得する予約情報の会員番号
        shardDBAssertUtil.shardUpdateTest("1arg", expectedReservations);
    }

    /**
     * DTSH0104 001 002 シャード1,2から1件の予約情報を削除できることを確認する(Serviceの引数が1つの時)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0104_001_002() throws ClassNotFoundException, SQLException {

        // 取得する予約情報の会員番号
        shardDBAssertUtil.shardDeleteTest("1arg", expectedReservations);
    }

    /**
     * DTSH0201 001 シャード1に予約番号が偶数の予約情報、シャード2に予約番号奇数の予約情報が登録されていることを確認する(Serviceの引数が2つの時)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0201_001() throws ClassNotFoundException, SQLException {
        shardDBAssertUtil.shardInsertTest("2arg", expectedReservations);
    }

    /**
     * DTSH0202 001 002 シャード1,2から予約1件取得できることを確認する(Serviceの引数が2つの時)
     */
    @Test
    public void testDTSH0202_001_002() {
        shardDBAssertUtil.shardSelectTest("2arg", expectedReservations);
    }

    /**
     * DTSH0203 001 002 シャード1,2から1件の予約情報を更新(旅行代金を0にする)できることを確認する(Serviceの引数が2つの時)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0203_001_002() throws ClassNotFoundException, SQLException {
        shardDBAssertUtil.shardUpdateTest("2arg", expectedReservations);
    }

    /**
     * DTSH0204 001 002 シャード1,2から1件の予約情報を削除できることを確認する(Serviceの引数が2つの時)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0204_001_002() throws ClassNotFoundException, SQLException {
        shardDBAssertUtil.shardDeleteTest("2arg", expectedReservations);
    }

    /**
     * DTSH0301 001 デフォルトDBに会員情報が登録できることを確認する
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0301_001() throws ClassNotFoundException, SQLException {

        given().get("member/register");

        // デフォルト DBに接続して会員情報が登録されていることを確認する。
        try (Connection conn = dbUtil.getDefaultConnection();
                Statement stmt = conn.createStatement(); ResultSet rset = stmt
                        .executeQuery(SQLConst.MEMBER.SELECT_ALL);) {

            int index = 0;
            while (rset.next()) {

                Member expected = expectedMembers.get(index);

                // アサート:会員情報が登録されていることを確認する。
                DBAssertUtil.assertDBMember(expected, rset);

                if (index < expectedMembers.size()) {
                    index++;
                } else {

                    // expectedMembers.size() 件以上登録されているはおかしいので、失敗にする。
                    fail("Result sets exceeded the prescribed number of row.");
                    break;
                }
            }
            if (index == 0) {
                fail("Default result set row is 0.");
            }
        }
    }

    /**
     * DTSH0302 001 デフォルトDBから1件取得できることを確認する
     */
    @Test
    public void testDTSH0302_001() {

        // 取得する会員情報
        Member expected = expectedMembers.get(0);

        HomePage homePage = open(applicationContextUrl
                + "member/get?customerNo=" + expected.getCustomerNo(),
                HomePage.class);

        // アサート:会員情報を1件取得できることを確認する。
        DBAssertUtil.assertViewMember(expected, homePage);

        // 証跡取得
        screenshot("defaultDBSelectTest");
    }

    /**
     * DTSH0303 001 デフォルトDBから1件の会員情報を更新(会員氏名(ふりがな)を"-"にする)できることを確認する
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0303_001() throws ClassNotFoundException, SQLException {

        // 取得する会員情報の会員番号
        Member expected = expectedMembers.get(0);
        expected.setFuriName("-");

        given().get("member/update?customerNo=" + expected.getCustomerNo());

        // デフォルト DBに接続して会員情報が更新されていることを確認する。
        try (Connection conn = dbUtil.getDefaultConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SQLConst.MEMBER.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);) {

            pstmt.setString(1, expected.getCustomerNo());
            try (ResultSet rset = pstmt.executeQuery()) {
                while (rset.next()) {

                    // アサート:会員情報が変更されていることを確認する。
                    DBAssertUtil.assertDBMember(expected, rset);
                }

                // アサート:取得件数が0件でないことを確認する。
                rset.last();
                assertNotEquals(0, rset.getRow());
            }
        }
    }

    /**
     * DTSH0304 001 デフォルトDBから1件削除できることを確認する
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0304_001() throws ClassNotFoundException, SQLException {

        // 取得する会員情報の会員番号
        Member expected = expectedMembers.get(0);

        given().get("member/delete?customerNo=" + expected.getCustomerNo());

        // デフォルト DBに接続して会員情報が更新されていることを確認する。
        try (Connection conn = dbUtil.getDefaultConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SQLConst.MEMBER.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);) {

            pstmt.setString(1, expected.getCustomerNo());
            try (ResultSet rset = pstmt.executeQuery()) {

                // アサート:取得件数が0件であることを確認する。
                rset.last();
                assertEquals(0, rset.getRow());
            }

        }
    }

    /**
     * DTSH0401 001 {@link @ShardWithAccount}を付与したメソッドに引数がない時、デフォルトDBにアクセスすることを確認する
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testDTSH0401_001() throws ClassNotFoundException, SQLException {
        given().get("member/delete?all");

        // デフォルト DBに接続して会員情報が更新されていることを確認する。
        try (Connection conn = dbUtil.getDefaultConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SQLConst.MEMBER.SELECT_ALL,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); ResultSet rset = pstmt
                                .executeQuery();) {

            // アサート:取得件数が0件であることを確認する。
            rset.last();
            assertEquals(0, rset.getRow());
        }
    }
}
