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
package jp.co.ntt.cloud.functionaltest.config;

import javax.inject.Inject;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 環境リポジトリ登録クラス
 * @author NTT 電電太郎
 */
@Configuration
@EnableConfigurationProperties(ConfigServerProperties.class)
public class CustomEnvironmentRepositoryConfiguration {

    /**
     * spring profile が s3 のときに設定する環境レポジトリ
     * @author NTT 電電太郎
     */
    @Configuration
    @Profile(value = { "loginfo", "logwarn" })
    protected static class S3RepositoryConfiguration {

        @Inject
        private ConfigurableEnvironment environment;

        /**
         * 環境レポジトリをBean登録する。
         * @return S3 環境レポジトリ
         */
        @Bean
        public EnvironmentRepository environmentRepository() {
            return new S3EnvironmentRepository(this.environment);
        }

    }
}
