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
package jp.co.ntt.cloud.functionaltest.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.web.servlet.WebMvcMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server メインクラス
 * @author NTT 電電太郎
 */
@SpringBootApplication
@EnableConfigServer
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
        JmxAutoConfiguration.class, WebMvcAutoConfiguration.class,
        SecurityAutoConfiguration.class, WebMvcMetricsAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class, })
public class ConfigServer {

    /**
     * メインメソッド
     * @param args コマンド引数
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
    }
}
