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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.pool;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.terasoluna.gfw.common.exception.SystemException;

import jp.co.ntt.cloud.functionaltest.domain.common.logging.LogMessages;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.DataSourceFactory;

/**
 * Tomcatデータソースのファクトリ。
 * @author NTT 電電太郎
 */
public class TomcatDataSourceFactory implements DataSourceFactory {

    /**
     * Tomcatのデータソースファクトリ。
     */
    private org.apache.tomcat.jdbc.pool.DataSourceFactory factory = new org.apache.tomcat.jdbc.pool.DataSourceFactory();

    @Override
    public DataSource create(Map<String, String> dataSourceProperties,
            Map<String, String> commonDataSourceProperties) {
        DataSource ret = null;
        Properties properties = new Properties();

        if (!commonDataSourceProperties.isEmpty()) {
            properties.putAll(commonDataSourceProperties);
        }

        properties.putAll(dataSourceProperties);
        try {
            ret = factory.createDataSource(properties);
        } catch (Exception e) {
            throw new SystemException(LogMessages.E_AR_A0_L9008
                    .getCode(), LogMessages.E_AR_A0_L9008.getMessage(), e);
        }
        return ret;
    }

}
