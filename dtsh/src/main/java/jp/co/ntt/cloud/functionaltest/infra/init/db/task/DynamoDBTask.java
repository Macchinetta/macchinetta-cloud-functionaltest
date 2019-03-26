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
package jp.co.ntt.cloud.functionaltest.infra.init.db.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

/**
 * DynamoDBのAntタスク。
 * @author NTT 電電花子
 */
public class DynamoDBTask extends Task {

    private AmazonDynamoDB dynamoDB;

    private String region;

    private String dbDriver;

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    private String tablename;

    private String tableidname;

    private String tablevaluename;

    private String shardDataSourceKeys;

    private String proxyHost;

    private String proxyPort;

    private String proxyUsername;

    private String proxyPassword;

    private static final String VALIDATION_MESSAGE = "%sの設定は必須です。";

    /**
     * @param shardDataSourceKeys セットする shardDataSourceKeys
     */
    public void setShardDataSourceKeys(String shardDataSourceKeys) {
        if (shardDataSourceKeys != null && shardDataSourceKeys.startsWith(
                "${")) {
            shardDataSourceKeys = null;
        }
        this.shardDataSourceKeys = shardDataSourceKeys;
    }

    /**
     * @param tableidname セットする tableidname
     */
    public void setTableidname(String tableidname) {
        if (tableidname != null && tableidname.startsWith("${")) {
            tableidname = null;
        }
        this.tableidname = tableidname;
    }

    /**
     * @param tablevaluename セットする tablevaluename
     */
    public void setTablevaluename(String tablevaluename) {
        if (tablevaluename != null && tablevaluename.startsWith("${")) {
            tablevaluename = null;
        }
        this.tablevaluename = tablevaluename;
    }

    /**
     * @param tablename セットする tablename
     */
    public void setTablename(String tablename) {
        if (tablename != null && tablename.startsWith("${")) {
            tablename = null;
        }
        this.tablename = tablename;
    }

    /**
     * @param region セットする endpoint
     */
    public void setRegion(String region) {
        if (region != null && region.startsWith("${")) {
            region = null;
        }
        this.region = region;
    }

    /**
     * @param dbDriver セットする dbDriver
     */
    public void setDbDriver(String dbDriver) {
        if (dbDriver != null && dbDriver.startsWith("${")) {
            dbDriver = null;
        }
        this.dbDriver = dbDriver;
    }

    /**
     * @param dbUrl セットする dbUrl
     */
    public void setDbUrl(String dbUrl) {
        if (dbUrl != null && dbUrl.startsWith("${")) {
            dbUrl = null;
        }
        this.dbUrl = dbUrl;
    }

    /**
     * @param dbUsername セットする dbUsername
     */
    public void setDbUsername(String dbUsername) {
        if (dbUsername != null && dbUsername.startsWith("${")) {
            dbUsername = null;
        }
        this.dbUsername = dbUsername;
    }

    /**
     * @param dbPassword セットする dbPassword
     */
    public void setDbPassword(String dbPassword) {
        if (dbPassword != null && dbPassword.startsWith("${")) {
            dbPassword = null;
        }
        this.dbPassword = dbPassword;
    }

    /**
     * @param proxyHost セットする proxyHost
     */
    public void setProxyHost(String proxyHost) {
        if (proxyHost != null && proxyHost.startsWith("${")) {
            proxyHost = null;
        }
        this.proxyHost = proxyHost;
    }

    /**
     * @param proxyPort セットする proxyPort
     */
    public void setProxyPort(String proxyPort) {
        if (proxyPort != null && proxyPort.startsWith("${")) {
            proxyPort = null;
        }
        this.proxyPort = proxyPort;
    }

    /**
     * @param proxyUsername セットする proxyUsername
     */
    public void setProxyUsername(String proxyUsername) {
        if (proxyUsername != null && proxyUsername.startsWith("${")) {
            proxyUsername = null;
        }
        this.proxyUsername = proxyUsername;
    }

    /**
     * @param proxyPassword セットする proxyPassword
     */
    public void setProxyPassword(String proxyPassword) {
        if (proxyPassword != null && proxyPassword.startsWith("${")) {
            proxyPassword = null;
        }
        this.proxyPassword = proxyPassword;
    }

    /*
     * (非 Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {
        try {
            validate();

            System.out.println(
                    "*********************** DynanoDBを初期化します。Endpoint=" + region
                            + " TableName=" + tablename
                            + " ***********************");

            if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(
                    proxyPort)) {
                ClientConfiguration clientConfiguration = new ClientConfiguration();
                clientConfiguration.setProtocol(Protocol.HTTPS);
                clientConfiguration.setProxyHost(proxyHost);
                clientConfiguration.setProxyPort(Integer.parseInt(proxyPort));
                clientConfiguration.setProxyUsername(proxyUsername);
                clientConfiguration.setProxyPassword(proxyPassword);
                dynamoDB = AmazonDynamoDBClientBuilder.standard()
                        .withClientConfiguration(clientConfiguration)
                        .withRegion(region).build();
            } else {
                dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(
                        region).build();
                ;
            }

            createTable();

            Map<String, List<WriteRequest>> requestItems = new HashMap<>();
            List<WriteRequest> writeRequests = getShardingAccounts();
            if (!writeRequests.isEmpty()) {
                for (int i = 0; i < writeRequests.size(); i++) {
                    requestItems.put(tablename, writeRequests.subList(i, i
                            + 1));
                    dynamoDB.batchWriteItem(requestItems);
                }
            }
            System.out.println(
                    "*********************** DynanoDBを初期化しました。Endpoint="
                            + region + " TableName=" + tablename
                            + " ***********************");
        } catch (Exception e) {
            throw new BuildException("DynamoDB初期化処理が失敗しました。", e);
        }
    }

    /**
     * DynamoDBのテーブルを作成する。
     * @throws InterruptedException
     */
    private void createTable() throws InterruptedException {
        if (exists()) {
            dynamoDB.deleteTable(tablename);
        }
        while (exists()) {
            Thread.sleep(1000L);
        }
        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement().withAttributeName(tableidname)
                .withKeyType(KeyType.HASH));

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName(
                tableidname).withAttributeType("S"));

        CreateTableRequest request = new CreateTableRequest().withTableName(
                tablename).withKeySchema(keySchema).withAttributeDefinitions(
                        attributeDefinitions).withProvisionedThroughput(
                                new ProvisionedThroughput()
                                        .withReadCapacityUnits(5L)
                                        .withWriteCapacityUnits(5L));
        dynamoDB.createTable(request);

    }

    /**
     * 存在チェック。
     * @return
     */
    private boolean exists() {
        boolean exists;
        try {
            TableUtils.waitUntilExists(dynamoDB, tablename, 1000, 100);
            exists = true;
        } catch (final AmazonClientException ex) {
            exists = false;
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        return exists;
    }

    /**
     * DynamoDBに登録するシャードアカウントデータを取得する。
     * @return
     * @throws Exception
     */
    private List<WriteRequest> getShardingAccounts() throws SQLException, ClassNotFoundException {
        List<WriteRequest> requests = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            Class.forName(dbDriver);
            // コネクション取得
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            // ステートメントを作成
            stmt = conn.createStatement();
            // 問合せの実行
            rset = stmt.executeQuery("SELECT customer_no FROM member");

            // 問合せ結果の表示
            while (rset.next()) {
                Map<String, AttributeValue> attr = new HashMap<>();
                attr.put(tableidname, new AttributeValue(rset.getString(1)));
                String dataSourceKey = getDataSourceKey(rset.getInt(1));
                attr.put(tablevaluename, new AttributeValue(dataSourceKey));
                PutRequest request = new PutRequest(attr);
                requests.add(new WriteRequest(request));
            }
        } finally {
            // 結果セットをクローズ
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // ステートメントをクローズ
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // 接続をクローズ
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return requests;
    }

    private String getDataSourceKey(int key) {
        String[] shardKeys = shardDataSourceKeys.split(",");
        int dataSourceIndex = key % (shardKeys.length);
        return shardKeys[dataSourceIndex];
    }

    private void validate() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(region)) {
            sb.append("エンドポイント");
        }
        if (StringUtils.isEmpty(dbDriver)) {
            sb.append(" データベースドライバ");
        }
        if (StringUtils.isEmpty(dbUrl)) {
            sb.append(" データベースURL");
        }
        if (StringUtils.isEmpty(dbUsername)) {
            sb.append(" データベースユーザ名");
        }
        if (StringUtils.isEmpty(dbPassword)) {
            sb.append(" データベースパスワード");
        }
        if (StringUtils.isEmpty(shardDataSourceKeys)) {
            sb.append(" シャードデータデータソースキーの配列");
        }
        if (sb.length() > 0) {
            throw new BuildException(String.format(VALIDATION_MESSAGE, sb
                    .toString()));
        }
    }
}
