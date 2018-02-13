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
package jp.co.ntt.cloud.functionaltest.db;

/**
 * テスト用SQL文
 *
 * @author NTT 電電太郎
 *
 */
public class SQLConst {

    /**
     * MEMBER テーブル
     */
    public static class MEMBER {
        public static final String TRUNCATE = "TRUNCATE TABLE member";
        public static final String SELECT_ALL = "SELECT customer_no, name, furi_name FROM member ORDER BY customer_no ASC";
        public static final String SELECT_FIND_ONE = "SELECT customer_no, name, furi_name FROM member WHERE customer_no=?";
    }

    /**
     * RESERVATION テーブル
     */
    public static class RESERVATION {
        public static final String TRUNCATE = "TRUNCATE TABLE reservation";
        public static final String SELECT_ALL = "SELECT reserve_no, reserve_date, total_fare, rep_customer_no, rep_name, rep_furi_name FROM reservation ORDER BY reserve_no ASC";
        public static final String SELECT_FIND_ONE = "SELECT reserve_no, reserve_date, total_fare, rep_customer_no, rep_name, rep_furi_name FROM reservation WHERE reserve_no = ?";
    }
}
