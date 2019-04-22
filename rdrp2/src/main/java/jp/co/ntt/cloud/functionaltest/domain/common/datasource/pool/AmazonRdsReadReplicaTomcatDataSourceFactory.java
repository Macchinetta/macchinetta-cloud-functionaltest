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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.jdbc.datasource.ReadOnlyRoutingDataSource;
import org.springframework.cloud.aws.jdbc.datasource.support.DatabaseType;
import org.springframework.cloud.aws.jdbc.datasource.support.StaticDatabasePlatformSupport;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.util.StringUtils;
import org.terasoluna.gfw.common.exception.SystemException;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;

import jp.co.ntt.cloud.functionaltest.domain.common.logging.LogMessages;

/**
 * Amazon RDS Read-Replica Tomcatデータソースのファクトリ。
 * @author NTT 電電花子
 */
public class AmazonRdsReadReplicaTomcatDataSourceFactory extends
                                                         TomcatDataSourceFactory {

    /**
     * デフォルトリージョン
     */
    @Value("${database.rdsRegion}")
    private String defaultRegion;

    /**
     * ドライバURLオプション
     */
    private static final String driverUrlOptionKey = "driverUrlOption";

    /**
     * リードレプリカデータベースのリージョン
     */
    private static final String replicaRegionKey = "replicaRegion";

    /**
     * ドライバクラス名
     */
    private static final String driverClassNameKey = "driverClassName";

    /**
     * {@link StaticDatabasePlatformSupport}
     */
    private StaticDatabasePlatformSupport databasePlatformSupport = new StaticDatabasePlatformSupport();

    /**
     * リードレプリカのデータソースを作成する。指定されたデータベースIDがリードレプリカ構成の場合は{@link ReadOnlyRoutingDataSource}を
     * 作成し、リードレプリカ構成でない場合は{@link org.apache.tomcat.jdbc.pool.DataSource}を作成し返却する。
     * @param properties
     * @return {@link DataSource}
     * @throws Exception
     */
    @Override
    protected DataSource createReadReplicaDataSource(
            Properties properties) throws Exception {
        String region = defaultRegion;
        if (!StringUtils.isEmpty(properties.getProperty(replicaRegionKey))) {
            region = properties.getProperty(replicaRegionKey);
        }
        AmazonRDS amazonRds = AmazonRDSClientBuilder.standard().withRegion(
                region).build();

        String dbInstanceIdentifier = (String) properties.get(
                dbInstanceIdentifierKey);
        DBInstance dbInstance = getDbInstance(amazonRds, dbInstanceIdentifier);

        if (dbInstance.getReadReplicaDBInstanceIdentifiers().isEmpty()) {
            return createDataSourceInstance(dbInstance, properties);
        }

        Map<Object, Object> replicaMap = new HashMap<>(dbInstance
                .getReadReplicaDBInstanceIdentifiers().size());

        for (String replicaName : dbInstance
                .getReadReplicaDBInstanceIdentifiers()) {
            replicaMap.put(replicaName, createDataSourceInstance(amazonRds,
                    replicaName, properties));
        }

        // Create the data source
        ReadOnlyRoutingDataSource dataSource = new ReadOnlyRoutingDataSource();
        dataSource.setTargetDataSources(replicaMap);
        dataSource.setDefaultTargetDataSource(createDataSourceInstance(
                dbInstance, properties));

        // Initialize the class
        dataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(dataSource);
    }

    /**
     * RDSのデータベースインスタンスを取得する。
     * @param identifier データベースID
     * @return {@link DBInstance}
     * @throws IllegalStateException
     */
    private DBInstance getDbInstance(AmazonRDS amazonRds,
            String identifier) throws IllegalStateException {
        DBInstance instance;
        try {
            DescribeDBInstancesResult describeDBInstancesResult = amazonRds
                    .describeDBInstances(new DescribeDBInstancesRequest()
                            .withDBInstanceIdentifier(identifier));
            instance = describeDBInstancesResult.getDBInstances().get(0);
        } catch (DBInstanceNotFoundException e) {
            throw new SystemException(LogMessages.E_AR_A0_L9009
                    .getCode(), LogMessages.E_AR_A0_L9009.getMessage(
                            identifier), e);
        }
        return instance;
    }

    /**
     * データソースを作成する。
     * @param identifier データベースID
     * @param properties {@link Properties}
     * @return {@link DataSource}
     * @throws Exception
     */
    private DataSource createDataSourceInstance(AmazonRDS amazonRds,
            String identifier, Properties properties) throws Exception {
        DBInstance instance = getDbInstance(amazonRds, identifier);
        return createDataSourceInstance(instance, properties);
    }

    /**
     * @param instance {@link DBInstance}
     * @param properties {@link Properties}
     * @return {@link DataSource}
     * @throws Exception
     */
    private DataSource createDataSourceInstance(DBInstance instance,
            Properties properties) throws Exception {
        properties.setProperty("url", createUrl(instance, properties));
        if (!properties.containsKey(driverClassNameKey)) {
            properties.setProperty(driverClassNameKey, getDriverClassName(
                    instance));
        }
        return factory.createDataSource(properties);
    }

    /**
     * データベースURLを作成する。
     * @param instance {@link DBInstance}
     * @param properties {@link Properties}
     * @return データベースURL
     */
    private String createUrl(DBInstance instance, Properties properties) {
        StringBuilder sb = new StringBuilder();
        String url = databasePlatformSupport.getDatabaseUrlForDatabase(
                DatabaseType.fromEngine(instance.getEngine()), instance
                        .getEndpoint().getAddress(), instance.getEndpoint()
                                .getPort(), instance.getDBName());
        sb.append(url);
        if (properties.containsKey(driverUrlOptionKey)) {
            sb.append("?").append(properties.getProperty(driverUrlOptionKey));
        }
        return sb.toString();
    }

    /**
     * ドライバクラス名を取得する。
     * @param instance {@link DBInstance}
     * @return ドライバクラス名
     */
    private String getDriverClassName(DBInstance instance) {
        return databasePlatformSupport.getDriverClassNameForDatabase(
                DatabaseType.fromEngine(instance.getEngine()));
    }

}
