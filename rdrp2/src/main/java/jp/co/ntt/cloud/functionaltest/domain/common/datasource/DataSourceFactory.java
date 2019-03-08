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
package jp.co.ntt.cloud.functionaltest.domain.common.datasource;

import java.util.Map;

import javax.sql.DataSource;

/**
 * データソースファクトリインタフェース。
 * @author NTT 電電花子
 */
public interface DataSourceFactory {

    /**
     * 引数の個別設定情報を優先して共通設定情報とマージし、{@link DataSource}を作成する。
     * @param dataSourceProperties 個別設定情報
     * @param commonDataSourceProperties 共通設定情報
     * @return {@link DataSource}
     */
    DataSource create(Map<String, String> dataSourceProperties,
            Map<String, String> commonDataSourceProperties);
}
