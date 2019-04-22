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
package jp.co.ntt.cloud.functionaltest.domain.common.datasource.pool;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.terasoluna.gfw.common.exception.SystemException;

import jp.co.ntt.cloud.functionaltest.domain.common.datasource.DataSourceFactory;
import jp.co.ntt.cloud.functionaltest.domain.common.logging.LogMessages;

/**
 * Tomcatデータソースのファクトリ。
 * @author NTT 電電花子
 */
public class TomcatDataSourceFactory implements DataSourceFactory {

    /**
     * リードレプリカデータベースID
     */
    protected static final String dbInstanceIdentifierKey = "dbInstanceIdentifier";

    /**
     * Tomcatのデータソースファクトリ。
     */
    protected org.apache.tomcat.jdbc.pool.DataSourceFactory factory = new org.apache.tomcat.jdbc.pool.DataSourceFactory();

    /*
     * (非 Javadoc)
     * @see jp.co.ntt.atrs.domain.common.shard.datasource.DataSourceFactory#create(java.util.Map, java.util.Map)
     */
    @Override
    public DataSource create(Map<String, String> dataSourceProperties,
            Map<String, String> commonDataSourceProperties) {
        DataSource ret = null;
        Properties properties = new Properties();
        if (!commonDataSourceProperties.isEmpty()) {
            // 共通データソース設定を反映
            properties.putAll(commonDataSourceProperties);
        }
        // 個別データソース設定を反映(共通データソース設定を上書き)
        properties.putAll(dataSourceProperties);
        try {
            if (properties.containsKey(dbInstanceIdentifierKey)) {
                ret = createReadReplicaDataSource(properties);
            } else {
                ret = factory.createDataSource(properties);
            }
        } catch (Exception e) {
            throw new SystemException(LogMessages.E_AR_A0_L9008
                    .getCode(), LogMessages.E_AR_A0_L9008.getMessage(), e);
        }
        return ret;
    }

    /**
     * リードレプリカのデータソースを作成する。
     * @param properties
     * @return {@link DataSource}
     * @throws Exception
     */
    protected DataSource createReadReplicaDataSource(
            Properties properties) throws Exception {
        throw new SystemException(LogMessages.E_AR_A0_L9010
                .getCode(), LogMessages.E_AR_A0_L9010.getMessage(
                        dbInstanceIdentifierKey));
    }

}
