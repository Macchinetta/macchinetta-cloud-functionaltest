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
package jp.co.ntt.cloud.functionaltest.domain.common.datasource;

/**
 * データソースのキー(シャードキー)を保持するホルダ。
 * @author NTT 電電太郎
 */
public class RoutingDataSourceLookupKeyHolder {

    /**
     * データソースのキーを保持する{@link ThreadLocal}。
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * データソースのキーを設定する。
     * @param dataSourceKey データソースのキー
     */
    public void set(String dataSourceKey) {
        contextHolder.set(dataSourceKey);
    }

    /**
     * データソースのキーを取得する。
     * @return データソースのキー
     */
    public String get() {
        return contextHolder.get();
    }

    /**
     * 保持したデータソースのキーを削除する。
     */
    public void clear() {
        contextHolder.remove();
    }
}
