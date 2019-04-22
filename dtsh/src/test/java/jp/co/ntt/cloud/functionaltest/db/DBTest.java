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
package jp.co.ntt.cloud.functionaltest.db;

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

import io.restassured.RestAssured;
import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import junit.framework.TestCase;

/**
 * @author NTT 電電太郎
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBTest extends TestCase {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * テスト結果の出力先
     */
    @Value("${path.report}")
    private String reportPath;

    @Inject
    private DBUtil dbUtil;

    /**
     * 会員情報の期待値リスト
     */
    private List<Member> expectedMembers = null;

    /**
     * 予約情報の期待値リスト
     */
    private List<Reservation> expectedReservations = null;

    @Inject
    private ShardDBTestHelper dbTestHelper;

    @Override
    @Before
    public void setUp() {
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
     * デフォルトDBに会員情報が登録されていること
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db01DefaultInsertTest() throws SQLException, ClassNotFoundException {

        given().get("member/register");

        // デフォルト DBに接続して会員情報が登録されていることを確認する。
        try ( // @formatter:off
                Connection conn = dbUtil.getDefaultConnection();
                Statement stmt = conn.createStatement(); ResultSet rset = stmt
                        .executeQuery(SQLConst.MEMBER.SELECT_ALL);) {
            // @formatter:on

            int index = 0;
            while (rset.next()) {

                Member expected = expectedMembers.get(index);
                AssertHelper.assertDBMember(expected, rset);

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
     * デフォルトDBから1件取得できること
     * @throws SQLException
     */
    @Test
    public void db02DefaultSelectTest() {
        // 取得する会員情報
        Member expected = expectedMembers.get(0);

        HelloPage helloPage = open(applicationContextUrl
                + "member/get?customerNo=" + expected.getCustomerNo(),
                HelloPage.class);

        AssertHelper.assertViewMember(expected, helloPage);
        // 証跡取得
        screenshot("defaultDBSelectTest");
    }

    /**
     * デフォルトDBから1件の会員情報を更新(会員氏名(ふりがな)を"-"にする)できること
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db03DefaultUpdateTest() throws SQLException, ClassNotFoundException {

        // 取得する会員情報の会員番号
        Member expected = expectedMembers.get(0);
        expected.setFuriName("-");

        given().get("member/update?customerNo=" + expected.getCustomerNo());

        // デフォルト DBに接続して会員情報が更新されていることを確認する。
        try ( // @formatter:off
                Connection conn = dbUtil.getDefaultConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SQLConst.MEMBER.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);) {
            // @formatter:on

            pstmt.setString(1, expected.getCustomerNo());
            try (ResultSet rset = pstmt.executeQuery()) {
                while (rset.next()) {
                    AssertHelper.assertDBMember(expected, rset);
                }
                // 取得件数が0件でないことの確認
                rset.last();
                assertNotEquals(0, rset.getRow());
            }
        }
    }

    /**
     * デフォルトDBから1件削除できること
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db04DefaultDeleteTest() throws ClassNotFoundException, SQLException {

        // 取得する会員情報の会員番号
        Member expected = expectedMembers.get(0);

        given().get("member/delete?customerNo=" + expected.getCustomerNo());

        // デフォルト DBに接続して会員情報が更新されていることを確認する。
        try ( // @formatter:off
                Connection conn = dbUtil.getDefaultConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SQLConst.MEMBER.SELECT_FIND_ONE,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);) {
            // @formatter:on

            pstmt.setString(1, expected.getCustomerNo());
            try (ResultSet rset = pstmt.executeQuery()) {

                // 取得件数が0件であることの確認
                rset.last();
                assertEquals(0, rset.getRow());
            }

        }
    }

    /**
     * シャード1に予約番号が偶数の予約情報、シャード2に予約番号奇数の予約情報が登録されていること(Serviceの引数が1つの時)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db05Shard1ArgInsertTest() throws ClassNotFoundException, SQLException {
        dbTestHelper.shardInsertTest("1arg", expectedReservations);
    }

    /**
     * シャード1,2から予約1件取得できること(Serviceの引数が1つの時)
     */
    @Test
    public void db06Shard1ArgSelectTest() {
        dbTestHelper.shardSelectTest("1arg", expectedReservations);
    }

    /**
     * シャード1,2から1件の予約情報を更新(旅行代金を0にする)できること(Serviceの引数が1つの時)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db07Shard1ArgUpdateTest() throws ClassNotFoundException, SQLException {
        // 取得する予約情報の会員番号
        dbTestHelper.shardUpdateTest("1arg", expectedReservations);
    }

    /**
     * シャード1,2から1件の予約情報を削除できること(Serviceの引数が1つの時)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db08Shard1ArgDeleteTest() throws ClassNotFoundException, SQLException {
        // 取得する予約情報の会員番号
        dbTestHelper.shardDeleteTest("1arg", expectedReservations);
    }

    /**
     * シャード1に予約番号が偶数の予約情報、シャード2に予約番号奇数の予約情報が登録されていること(Serviceの引数が2つの時)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db09Shard2ArgInsertTest() throws ClassNotFoundException, SQLException {
        dbTestHelper.shardInsertTest("2arg", expectedReservations);
    }

    /**
     * シャード1,2から予約1件取得できること(Serviceの引数が2つの時)
     */
    @Test
    public void db10Shard2ArgSelectTest() {
        dbTestHelper.shardSelectTest("2arg", expectedReservations);
    }

    /**
     * シャード1,2から1件の予約情報を更新(旅行代金を0にする)できること(Serviceの引数が2つの時)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db11Shard2ArgUpdateTest() throws ClassNotFoundException, SQLException {
        dbTestHelper.shardUpdateTest("2arg", expectedReservations);
    }

    /**
     * シャード1,2から1件の予約情報を削除できること(Serviceの引数が2つの時)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db12Shard2ArgDeleteTest() throws ClassNotFoundException, SQLException {
        dbTestHelper.shardDeleteTest("2arg", expectedReservations);
    }

    /**
     * {@link @ShardWithAccount}を付与したメソッドに引数がない時、デフォルトDBにアクセスすること
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void db13ShardWithAccountIsNull() throws SQLException, ClassNotFoundException {
        given().get("member/delete?all");

        // デフォルト DBに接続して会員情報が更新されていることを確認する。
        try ( // @formatter:off
                Connection conn = dbUtil.getDefaultConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SQLConst.MEMBER.SELECT_ALL,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); ResultSet rset = pstmt
                                .executeQuery();) {
            // @formatter:on

            // 取得件数が0件であることの確認
            rset.last();
            assertEquals(0, rset.getRow());
        }
    }
}
