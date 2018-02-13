/*
 * Copyright 2014-2017 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * データソース個別情報。
 *
 * @author NTT 電電太郎
 *
 */
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {

    private List<Map<String, String>> dataSources = new ArrayList<>();

    /**
     * @return the dataSources
     */
    public List<Map<String, String>> getDataSources() {
        return dataSources;
    }

    /**
     * @param dataSources
     *            the dataSources to set
     */
    public void setDataSources(List<Map<String, String>> dataSources) {
        this.dataSources = dataSources;
    }

}
