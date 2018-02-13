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
package jp.co.ntt.cloud.functionaltest.domain.common.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import jp.co.ntt.cloud.functionaltest.domain.common.datasource.model.DatabaseProperties;


/**
 * データソースキーのリゾルバ。
 * @author NTT 電電花子
 */
public class DataSourceKeyResolver implements ShardKeyResolver, InitializingBean {

    /**
     * デフォルトスキーマ名。<br>
     * 設定が無いときは"<em>default</em>"となる。
     */
    @Value("${database.default.schema.name:default}")
    private String databaseDefaultSchemaName;

    /**
     * データソース情報のリストを保持するクラス。
     */
    private DatabaseProperties databaseProperties;

    /**
     * シャード用データソースキーのリスト。
     */
    private List<Map<String, String>> dataSources;

    /**
     * コンストラクタ。
     * @param dataSourceConfig
     */
    public DataSourceKeyResolver(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    /**
     * シャード用データソースキーのリストを作成する。
     */
    @Override
    public void afterPropertiesSet() throws Exception  {
        this.dataSources = new ArrayList<>();
        for (Map<String, String> dataSource : this.databaseProperties
                .getDataSources()) {
            if (!databaseDefaultSchemaName.equals(dataSource
                    .get(ShardKeyResolver.SCHEMA_KEY_NAME))) {
                this.dataSources.add(dataSource);
            }
        }
    }

    /**
     * 引数のシャードキー(お客様番号)を元にシャードの割り当てをする。<br>
     * お客様番号をシャード用データソースキーのリストサイズで除算した余りをインデックスとして、
     * シャード用データソースキーのリストからデータソースキーを取得し返却する。
     */
    @Override
    public String resolveShardKey(String shardKey) {
        Integer key = Integer.valueOf(shardKey);
        int dataSourceIndex = key % (dataSources.size());
        Map<String, String> dataSource = dataSources.get(dataSourceIndex);
        return dataSource.get(ShardKeyResolver.SCHEMA_KEY_NAME);
    }

}
