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
package jp.co.ntt.cloud.functionaltest.infra.rdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author NTT 電電太郎
 */
public class RDBUtil {

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
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(driverClassName);
        return DriverManager.getConnection(urlDefault, user, password);
    }
}
