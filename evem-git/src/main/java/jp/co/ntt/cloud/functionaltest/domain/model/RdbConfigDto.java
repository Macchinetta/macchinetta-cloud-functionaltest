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
package jp.co.ntt.cloud.functionaltest.domain.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * プロパティファイルから取得したRDB関連の情報を保持するDtoクラス
 * @author NTT 電電太郎
 */
@Component
@ConfigurationProperties(prefix = "database")
public class RdbConfigDto {
    /**
     * RDB URL
     */
    private String url;

    /**
     * ファイル一時保存ディレクトリ
     */
    private String username;

    /**
     * ファイル保存ディレクトリ
     */
    private String password;

    /**
     * ドライバークラス名
     */
    private String driverClassName;

    /**
     * RDBのURLを取得する。
     * @return RDBのURL
     */
    public String getUrl() {
        return url;
    }

    /**
     * RDBのURLを設定する。
     * @param url RDBのURL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * RDBのユーザー名を取得する。
     * @return RDBのユーザー名
     */
    public String getUsername() {
        return username;
    }

    /**
     * RDBのユーザー名を設定する。
     * @param username RDBのユーザー名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * RDBのパスワードを取得する。
     * @return RDBのパスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * RDBのパスワードを設定する。
     * @param password RDBのパスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * RDBのドライバークラスを取得する。
     * @return RDBのドライバークラス
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * RDBのドライバークラスを設定する。
     * @param driverClassName RDBのドライバークラス
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

}
