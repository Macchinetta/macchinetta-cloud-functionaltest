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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author NTT 電電太郎
 */
public class DBUtil {

    /**
     * DB ドライバークラス
     */
    @Value("${database.common.data-source.driverClassName}")
    private String driverClassName;

    /**
     * デフォルトDBのURL
     */
    @Value("${database.data-sources.default.url}")
    private String urlDefault;

    /**
     * シャード1のURL
     */
    @Value("${database.data-sources.shard1.url}")
    private String urlShard1;

    /**
     * シャード2のURL
     */
    @Value("${database.data-sources.shard2.url}")
    private String urlShard2;

    /**
     * DBのuser
     */
    @Value("${database.common.data-source.username}")
    private String user;

    /**
     * DBのpassword
     */
    @Value("${database.common.data-source.password}")
    private String password;

    /**
     * DBのドライバークラスを設定する。
     * @param driverClassName ドライバークラス名
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * デフォルトDBのURLを設定する。
     * @param urlDefault デフォルトDBのURL
     */
    public void setUrlDefault(String urlDefault) {
        this.urlDefault = urlDefault;
    }

    /**
     * シャード1のURLを設定する。
     * @param urlShard1 シャード1のURL
     */
    public void setUrlShard1(String urlShard1) {
        this.urlShard1 = urlShard1;
    }

    /**
     * シャード2のURLを設定する。
     * @param urlShard2 シャード2のURL
     */
    public void setUrlShard2(String urlShard2) {
        this.urlShard2 = urlShard2;
    }

    /**
     * DBのユーザー名を設定する。
     * @param user DBのユーザー名
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * DBのパスワードを設定する。
     * @param password DBのパスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * デフォルトDBのコネクションを取得する。
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getDefaultConnection() throws SQLException, ClassNotFoundException {
        Class.forName(driverClassName);
        return DriverManager.getConnection(urlDefault, user, password);
    }

    /**
     * シャード1のコネクションを取得する。
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getShard1Connection() throws SQLException, ClassNotFoundException {
        Class.forName(driverClassName);
        return DriverManager.getConnection(urlShard1, user, password);
    }

    /**
     * シャード2のコネクションを取得する。
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getShard2Connection() throws SQLException, ClassNotFoundException {
        Class.forName(driverClassName);
        return DriverManager.getConnection(urlShard2, user, password);
    }
}
