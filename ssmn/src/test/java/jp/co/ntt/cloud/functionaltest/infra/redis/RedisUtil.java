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
package jp.co.ntt.cloud.functionaltest.infra.redis;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;

import jp.co.ntt.cloud.functionaltest.domain.model.Cart;
import jp.co.ntt.cloud.functionaltest.infra.common.ByteObjectConvarter;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * Redis操作クラス
 * @author NTT 電電太郎
 */
public class RedisUtil {

    /**
     * redisHost
     */
    private String redisHost;

    /**
     * RedisPort
     */
    private String redisPort;

    /**
     * Redis Cluster modeで設定する
     */
    private String redisClusterEndPoint;

    /**
     * No Cluster Mode Redis
     */
    private Jedis jedisSingleNode;

    /**
     * Redis Cluster
     */
    private JedisCluster jedisClusterNodes;

    @Value("${spring.session.key.search.term}")
    private String springSessionKeySearchTerm;

    private static final String VALIDATION_MESSAGE = "%sの設定は必須です。";

    public RedisUtil(String redisHost, String redisPort,
            String redisClusterEndPoint) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisClusterEndPoint = redisClusterEndPoint;

        validate();

        if (StringUtils.isEmpty(redisHost)) {
            this.jedisClusterNodes = createNodes(this.redisClusterEndPoint);
        } else {
            this.jedisSingleNode = createJedis(redisHost, Integer.valueOf(
                    redisPort));
        }

    }

    /**
     * 単一ノードの場合
     */
    private Jedis createJedis(String redisHost, int redisPort) {
        return new Jedis(redisHost, redisPort);
    }

    /**
     * クラスターノードの取得
     */
    private JedisCluster createNodes(String redisClusterNodes) {

        List<String> nodes = splitNodes(redisClusterNodes);
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        for (String node : nodes) {
            jedisClusterNodes.add(new HostAndPort(getHostFromEndPoint(
                    node), Integer.valueOf(getPortFromEndPoint(node))));
        }
        return new JedisCluster(jedisClusterNodes);

    }

    /**
     * Redis を初期化するメソッド
     * @throws IOException
     */
    public void flushRedis() throws IOException {
        validate();
        if (!StringUtils.isEmpty(redisHost)) {
            flushDB(redisHost, Integer.valueOf(redisPort));
        } else {
            List<String> nodes = splitNodes(redisClusterEndPoint);
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(nodes);
            JedisConnectionFactory factory = new JedisConnectionFactory(clusterConfiguration);
            factory.afterPropertiesSet();
            Iterator<RedisClusterNode> cnodes = factory.getClusterConnection()
                    .clusterGetNodes().iterator();
            while (cnodes.hasNext()) {
                RedisClusterNode node = cnodes.next();
                if (node.isMaster()) {
                    flushDB(node.getHost(), node.getPort());
                }
            }
        }
    }

    /**
     * Redis を初期化する
     * @throws IOException
     */
    private void flushDB(String redisHost,
            Integer redisPort) throws IOException {
        System.out.println("*********************** Redisマスターノード" + redisHost
                + ":" + redisPort + "をフラッシュします。 ***********************");
        Jedis jedis = new Jedis(redisHost, redisPort);
        String result = jedis.flushDB();
        jedis.close();
        System.out.println("*********************** ホスト " + redisHost
                + " Redis#flushDB() -> " + result + " ***********************");
        if (!"OK".equals(result)) {
            throw new IOException("RedisのflushDBに失敗しました。");
        }
        System.out.println("*********************** Redisマスターノード" + redisHost
                + ":" + redisPort + "をフラッシュしました。 ***********************");
    }

    /**
     * Redisに保存されている Cart の情報を取得する
     * @param sessionId
     * @throws BuildException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Cart getFromRedis(
            String sessionId) throws BuildException, ClassNotFoundException, IOException {
        byte[] key = ("spring:session:sessions:" + sessionId).getBytes();
        byte[] field = "sessionAttr:scopedTarget.cart".getBytes();

        if (StringUtils.isEmpty(redisHost)) {
            return (Cart) ByteObjectConvarter.toObject(jedisClusterNodes.hget(
                    key, field));
        } else {
            return (Cart) ByteObjectConvarter.toObject(jedisSingleNode.hget(key,
                    field));
        }
    }

    /**
     * Redisに保存されているSpring Sessionの数を取得する
     */
    public int getSpringSessionSize() {
        if (StringUtils.isEmpty(redisHost)) {

            Map<String, JedisPool> clusterNodes = jedisClusterNodes
                    .getClusterNodes();
            int keySize = 0;
            for (String k : clusterNodes.keySet()) {
                JedisPool jedisPool = clusterNodes.get(k);
                Jedis connection = jedisPool.getResource();
                keySize += connection.keys(springSessionKeySearchTerm).size();
            }
            return keySize;
        } else {
            return jedisSingleNode.keys(springSessionKeySearchTerm).size();
        }
    }

    /**
     * コンストラクタ入力項目チェック
     */
    private void validate() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(redisHost) && StringUtils.isEmpty(redisPort)
                && StringUtils.isEmpty(redisClusterEndPoint)) {
            sb.append("Redisホストと").append(" Redisポート").append(" または、").append(
                    " RedisClusterNodes");
        } else if (!StringUtils.isEmpty(redisHost) && StringUtils.isEmpty(
                redisPort) && StringUtils.isEmpty(redisClusterEndPoint)) {
            sb.append(" Redisポート");
        }
        if (sb.length() > 0) {
            throw new BuildException(String.format(VALIDATION_MESSAGE, sb
                    .toString()));
        }
    }

    /**
     * Redisノードエンドポイント文字列分割
     * @param redisClusterNodes Redisノードエンドポイント文字列
     * @return
     */
    private List<String> splitNodes(String redisClusterNodes) {
        String[] rcn = redisClusterNodes.split(",");
        return Arrays.asList(rcn);
    }

    /**
     * Redisのクラスターモード時にEndPointかホストを取得する
     * @param endPoint エンドポイント文字列
     * @return ポート番号
     */
    private String getHostFromEndPoint(String endPoint) {
        return endPoint.split(":")[0];
    }

    /**
     * Redisのクラスターモード時にEndPointからポート番号を取得する
     * @param endPoint エンドポイント文字列
     * @return ポート番号
     */
    private String getPortFromEndPoint(String endPoint) {
        return endPoint.split(":")[1];
    }
}
