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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.terasoluna.gfw.common.exception.SystemException;

import jp.co.ntt.cloud.functionaltest.domain.common.constants.Rdrp2Constants;
import jp.co.ntt.cloud.functionaltest.domain.common.datasource.model.CommonDatabaseProperties;
import jp.co.ntt.cloud.functionaltest.domain.common.datasource.model.DatabaseProperties;
import jp.co.ntt.cloud.functionaltest.domain.common.logging.LogMessages;

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
     * {@link ApplicationContext}。
     */
    @Inject
    ApplicationContext applicationContext;

    /**
     * {@link DefaultListableBeanFactory}。
     */
    @Inject
    DefaultListableBeanFactory factory;

    /**
     * コンストラクタ
     * @param databaseProperties データソース情報のリストを保持するクラス
     * @param CommonDatabaseProperties 共通のデータベース情報を保持するクラス。
     */
    public RoutingDataSourceBuilder(DatabaseProperties databaseProperties,
            CommonDatabaseProperties commonDatabaseProperties,
            DataSourceFactory dataSourceFactory) {
        if (databaseProperties.getDataSources().isEmpty()) {
            throw new SystemException(LogMessages.E_AR_A0_L9005
                    .getCode(), LogMessages.E_AR_A0_L9005.getMessage());
        }
        this.databaseProperties = databaseProperties;
        this.commonDatabaseProperties = commonDatabaseProperties;
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * シャードと非シャードのデータソースを作成する。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Map<String, String>> dataSources = databaseProperties
                .getDataSources();
        Map<Object, Object> targetDataSources = new HashMap<>();
        boolean defaultTargetDataSourceFlg = false;
        for (Map<String, String> dataSourceProperties : dataSources) {
            // データソースキー
            String sourceKey = dataSourceProperties.get(
                    Rdrp2Constants.SCHEMA_KEY_NAME);
            try {
                // データソースを作成
                javax.sql.DataSource source = dataSourceFactory.create(
                        dataSourceProperties, commonDatabaseProperties
                                .getDataSource());
                factory.registerSingleton(sourceKey, source);
            } catch (IllegalStateException e) {
                // データソースキーの重複
                throw new SystemException(LogMessages.E_AR_A0_L9007
                        .getCode(), LogMessages.E_AR_A0_L9007.getMessage(
                                sourceKey), e);
            } catch (Exception e) {
                // 予期せぬ例外
                throw new SystemException(LogMessages.E_AR_A0_L9008
                        .getCode(), LogMessages.E_AR_A0_L9008.getMessage(), e);
            }

            if (databaseDefaultSchemaName.equals(sourceKey)) {
                // 非シャードのデータソース
                this.defaultTargetDataSource = applicationContext.getBean(
                        sourceKey);
                defaultTargetDataSourceFlg = true;
            } else {
                // シャードのデータソース
                targetDataSources.put(sourceKey, applicationContext.getBean(
                        sourceKey));
            }
            this.targetDataSources = targetDataSources;
        }
        if (!defaultTargetDataSourceFlg) {
            // 非シャードのデータソースが設定されていない場合
            throw new SystemException(LogMessages.E_AR_A0_L9006
                    .getCode(), LogMessages.E_AR_A0_L9006.getMessage());
        }
        // テストのため削除

        // if (targetDataSources.isEmpty()) {
        // // シャードのデータソースが設定されていない場合。
        // throw new SystemException(LogMessages.E_AR_A0_L9005
        // .getCode(), LogMessages.E_AR_A0_L9005.getMessage());
        // }
        // this.targetDataSources = targetDataSources;
    }

    /**
     * シャードのデータソースを保持する{@link Map}を取得する。
     * @return targetDataSources
     */
    public Map<Object, Object> getTargetDataSources() {
        return targetDataSources;
    }

    /**
     * 非シャードのデータソースを保持するオブジェクトを取得する。
     * @return defaultTargetDataSource
     */
    public Object getDefaultTargetDataSource() {
        return defaultTargetDataSource;
    }
}
