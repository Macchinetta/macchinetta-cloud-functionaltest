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
package jp.co.ntt.cloud.functionaltest.initdb.task;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;

public class RedisTask extends Task {

    private String redisHost;

    private String redisPort;

    private String redisClusterNodes;

    private static final String VALIDATION_MESSAGE = "%sの設定は必須です。";

    /**
     * @param redisHost セットする redisHost
     */
    public void setRedisHost(String redisHost) {
        if (redisHost != null && redisHost.startsWith("${")) {
            redisHost = null;
        }
        this.redisHost = redisHost;
    }

    /**
     * @param redisPort セットする redisPort
     */
    public void setRedisPort(String redisPort) {
        if (redisPort != null && redisPort.startsWith("${")) {
            redisPort = null;
        }
        this.redisPort = redisPort;
    }

    /**
     * @param redisClusterNodes セットする redisClusterNodes
     */
    public void setRedisClusterNodes(String redisClusterNodes) {
        if (redisClusterNodes != null && redisClusterNodes.startsWith("${")) {
            redisClusterNodes = null;
        }
        this.redisClusterNodes = redisClusterNodes;
    }

    /*
     * (非 Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {
        validate();
        if (!StringUtils.isEmpty(redisHost)) {
            flushDB(redisHost, Integer.valueOf(redisPort));
        } else {
            String[] rcn = redisClusterNodes.split(",");
            List<String> nodes = Arrays.asList(rcn);
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

    private void flushDB(String redisHost, Integer redisPort) {
        System.out.println("*********************** Redisマスターノード" + redisHost
                + ":" + redisPort + "をフラッシュします。 ***********************");
        Jedis jedis = new Jedis(redisHost, redisPort);
        String result = jedis.flushDB();
        jedis.close();
        System.out.println("*********************** ホスト " + redisHost
                + " Redis#flushDB() -> " + result + " ***********************");
        if (!"OK".equals(result)) {
            throw new BuildException("RedisのflushDBに失敗しました。");
        }
        System.out.println("*********************** Redisマスターノード" + redisHost
                + ":" + redisPort + "をフラッシュしました。 ***********************");
    }

    private void validate() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(redisHost) && StringUtils.isEmpty(redisPort)
                && StringUtils.isEmpty(redisClusterNodes)) {
            sb.append("Redisホストと").append(" Redisポート").append(" または、").append(
                    " RedisClusterNodes");
        } else if (!StringUtils.isEmpty(redisHost) && StringUtils.isEmpty(
                redisPort) && StringUtils.isEmpty(redisClusterNodes)) {
            sb.append(" Redisポート");
        }
        if (sb.length() > 0) {
            throw new BuildException(String.format(VALIDATION_MESSAGE, sb
                    .toString()));
        }
    }
}
