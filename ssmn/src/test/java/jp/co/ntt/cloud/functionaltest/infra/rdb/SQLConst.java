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
package jp.co.ntt.cloud.functionaltest.infra.rdb;

/**
 * @author NTT 電電太郎
 *
 */
public class SQLConst {
    /**
     * TRANSACTION_TOKEN テーブル
     */
    public static class TRANSACTION_TOKEN {
        public static final String TRUNCATE = "TRUNCATE TABLE transaction_token";
        public static final String SELECT_ALL = "SELECT token_name, token_key, token_value, session_id, sequence FROM transaction_token ORDER BY sequence DESC";
        public static final String SELECT_FIND_ONE = "SELECT token_name, token_key, token_value, session_id, sequence FROM transaction_token WHERE session_id=?";
    }
}
