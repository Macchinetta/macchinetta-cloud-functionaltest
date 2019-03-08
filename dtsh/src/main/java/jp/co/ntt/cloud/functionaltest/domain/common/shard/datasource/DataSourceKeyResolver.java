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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.model.DatabaseProperties;

/**
 * シャードの割り当て決定。
 * @author NTT 電電太郎
 */
public class DataSourceKeyResolver implements ShardKeyResolver,
                                   InitializingBean {

    /**
     * デフォルトキーをインジェクト
     */
    @Value("${database.default.schema.name:default}")
    private String databaseDefaultSchemaName;

    /**
     * データソース個別情報プロパティクラス
     */
    private DatabaseProperties databaseProperties;

    /**
     * シャード用データソースキーのリスト
     */
    private List<Map<String, String>> dataSources;

    /**
     * @param databaseProperties データソース個別情報プロパティ
     */
    public DataSourceKeyResolver(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    /**
     * シャード用データソースキーのリスト作成する。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.dataSources = new ArrayList<>();
        for (Map<String, String> dataSource : this.databaseProperties
                .getDataSources()) {
            if (!databaseDefaultSchemaName.equals(dataSource.get(
                    ShardKeyResolver.SCHEMA_KEY_NAME))) {
                this.dataSources.add(dataSource);
            }
        }
    }

    /**
     * シャードキーを元にシャードの割り当てをする。
     */
    @Override
    public String resolveShardKey(Integer shardKey) {
        Integer dataSourceIndex = shardKey % (dataSources.size());
        Map<String, String> dataSource = dataSources.get(dataSourceIndex);
        return dataSource.get(ShardKeyResolver.SCHEMA_KEY_NAME);
    }

}
