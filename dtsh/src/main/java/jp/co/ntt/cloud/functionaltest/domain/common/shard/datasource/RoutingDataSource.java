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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 複数のデータソースを動的に切り替えるクラス。
 *
 * @author NTT 電電太郎
 *
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    /**
     * ロガー。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSource.class);

    @Value("${database.default.schema.name:default}")
    private String databaseDefaultSchemaName;

    private RoutingDataSourceLookUpKeyHolder dataSourceLookupKeyHolder;

    public RoutingDataSource(RoutingDataSourceBuilder routingDataSourceBuilder,
            RoutingDataSourceLookUpKeyHolder dataSourceLookUpKeyHolder) {
        super.setDefaultTargetDataSource(routingDataSourceBuilder.getDefaultTargetSource());
        super.setTargetDataSources(routingDataSourceBuilder.getTargetDataSources());
        this.dataSourceLookupKeyHolder = dataSourceLookUpKeyHolder;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String returnKey = dataSourceLookupKeyHolder.get();
        if (LOGGER.isDebugEnabled()) {
            String param = null == returnKey ? databaseDefaultSchemaName : returnKey;
            LOGGER.debug(String.format("シャードキー[ %s ]を選択しました。", param));
        }
        return returnKey;
    }

}
