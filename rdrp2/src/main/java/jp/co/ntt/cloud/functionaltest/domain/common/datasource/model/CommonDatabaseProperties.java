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
package jp.co.ntt.cloud.functionaltest.domain.common.datasource.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 共通のデータベース情報を保持するクラス。
 * @author NTT 電電太郎
 */
@ConfigurationProperties(prefix = "database.common")
public class CommonDatabaseProperties {

    /**
     * データソース情報を格納する {@link Map}
     */
    private Map<String, String> dataSource = new HashMap<>();

    /**
     * データソース情報を格納する {@link Map}を取得する。
     *
     * @return dataSource
     */
    public Map<String, String> getDataSource() {
        return dataSource;
    }

    /**
     * データソース情報を格納する {@link Map}を設定する。
     *
     * @param dataSource セットする dataSource
     */
    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

}
