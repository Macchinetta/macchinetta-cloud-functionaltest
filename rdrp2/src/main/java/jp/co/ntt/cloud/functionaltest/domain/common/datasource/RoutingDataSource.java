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
package jp.co.ntt.cloud.functionaltest.domain.common.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * ルーティングデータソースの実装。
 * @author NTT 電電太郎
 * @see AbstractRoutingDataSource
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    /**
     * ロガー。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            RoutingDataSource.class);

    /**
     * デフォルトスキーマ名。<br>
     * 設定が無いときは"<em>default</em>"となる。
     */
    @Value("${database.default.schema.name:default}")
    private String databaseDefaultSchemaName;

    /**
     * データソースのキー(シャードキー)を保持するホルダ。
     */
    private RoutingDataSourceLookupKeyHolder dataSourceLookupKeyHolder;

    /**
     * コンストラクタ
     * @param routingDataSourceBuilder ルーティングデータソースビルダ。
     * @param dataSourceLookupKeyHolder シャードキーホルダ
     */
    public RoutingDataSource(RoutingDataSourceBuilder routingDataSourceBuilder,
            RoutingDataSourceLookupKeyHolder dataSourceLookupKeyHolder) {
        super.setDefaultTargetDataSource(routingDataSourceBuilder
                .getDefaultTargetDataSource());
        super.setTargetDataSources(routingDataSourceBuilder
                .getTargetDataSources());
        this.dataSourceLookupKeyHolder = dataSourceLookupKeyHolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String returnKey = dataSourceLookupKeyHolder.get();
        if (LOGGER.isDebugEnabled()) {
            String param = null == returnKey ? databaseDefaultSchemaName
                    : returnKey;
            LOGGER.debug(String.format("シャードキー[ %s ]を選択しました。", param));
        }
        return returnKey;
    }
}
