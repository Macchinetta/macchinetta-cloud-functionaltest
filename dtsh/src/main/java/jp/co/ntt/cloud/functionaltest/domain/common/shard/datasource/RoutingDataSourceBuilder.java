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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.terasoluna.gfw.common.exception.SystemException;

import jp.co.ntt.cloud.functionaltest.domain.common.logging.LogMessages;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.model.CommonDatabaseProperties;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.model.DatabaseProperties;

/**
 * ルーティングデータソースを作成するビルダ。
 * @author NTT 電電太郎
 */
public class RoutingDataSourceBuilder implements InitializingBean {

    /**
     * デフォルトスキーマ名。<br>
     * 設定が無いときは"<em>default</em>"となる。
     */
    @Value("${database.default.schema.name:default}")
    private String databaseDefaultSchemaName;

    /**
     * シャードのデータソースを保持する{@link Map}。
     */
    private Map<Object, Object> targetDataSources;

    /**
     * 非シャードのデータソースを保持するオブジェクト。
     */
    private Object defaultTargetDataSource;

    /**
     * シャードのデータベース情報。
     */
    private DatabaseProperties databaseProperties;

    /**
     * 共通のデータベース情報。
     */
    private CommonDatabaseProperties commonDatabaseProperties;

    /**
     * データソースファクトリ。
     */
    private DataSourceFactory dataSourceFactory;

    /**
     * {@link ApplicationContext}。 一度登録したデータソースを取得するために使用する。
     */
    @Inject
    private ApplicationContext applicationContext;

    /**
     * {@link DefaultListableBeanFactory}。データソースを実行時に動的にインスタンス化し、SpringのDIコンテナに登録してBeanとして扱えるようにするために使用する。
     */
    @Inject
    private DefaultListableBeanFactory factory;

    public RoutingDataSourceBuilder(DatabaseProperties databaseProperties,
            CommonDatabaseProperties commonDatabaseProperties,
            DataSourceFactory dataSourceFactory) {
        this.databaseProperties = databaseProperties;
        this.commonDatabaseProperties = commonDatabaseProperties;
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * データソースの作成。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Map<String, String>> dataSources = databaseProperties
                .getDataSources();

        Map<Object, Object> targetDataSources = new HashMap<>();

        boolean defaultTargetDataSourceFlg = false;

        for (Map<String, String> dataSourceProperties : dataSources) {
            // プロパティファイルの database.data-soruces.schema の値を取得
            String sourceKey = dataSourceProperties.get(
                    ShardKeyResolver.SCHEMA_KEY_NAME);
            try {
                javax.sql.DataSource source = dataSourceFactory.create(
                        dataSourceProperties, commonDatabaseProperties
                                .getDataSource());
                factory.registerSingleton(sourceKey, source);
            } catch (IllegalStateException e) {
                throw new SystemException(LogMessages.E_AR_A0_L9007
                        .getCode(), LogMessages.E_AR_A0_L9007.getMessage(
                                sourceKey), e);
            } catch (Exception e) {
                throw new SystemException(LogMessages.E_AR_A0_L9008
                        .getCode(), LogMessages.E_AR_A0_L9008.getCode(), e);
            }

            if (databaseDefaultSchemaName.equals(sourceKey)) {
                // 非シャード のデータソースを保持する。
                this.defaultTargetDataSource = applicationContext.getBean(
                        sourceKey);
                defaultTargetDataSourceFlg = true;
            } else {
                // シャードのデータソースを保持する。
                targetDataSources.put(sourceKey, applicationContext.getBean(
                        sourceKey));
            }
        }

        if (!defaultTargetDataSourceFlg) {
            throw new SystemException(LogMessages.E_AR_A0_L9006
                    .getCode(), LogMessages.E_AR_A0_L9006.getMessage());
        }
        if (targetDataSources.isEmpty()) {
            throw new SystemException(LogMessages.E_AR_A0_L9005
                    .getCode(), LogMessages.E_AR_A0_L9005.getMessage());
        }
        this.targetDataSources = targetDataSources;

    }

    /**
     * シャードのデータソースを取得する。
     * @return シャードのデータソース
     */
    public Map<Object, Object> getTargetDataSources() {
        return targetDataSources;
    }

    /**
     * 非シャードのデータソースを取得する。
     * @return 非シャードのデータソース
     */
    public Object getDefaultTargetSource() {
        return defaultTargetDataSource;
    }

}
