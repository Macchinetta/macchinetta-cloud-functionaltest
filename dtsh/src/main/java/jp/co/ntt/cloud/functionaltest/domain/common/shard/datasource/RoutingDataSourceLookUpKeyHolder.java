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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource;

/**
 * データソースとシャードキーのマッピングを保持するクラス。
 * @author NTT 電電太郎
 */
public class RoutingDataSourceLookUpKeyHolder {

    /**
     * スレッドごとにデータソースキーを保持するクラス
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * データソースキーを設定する。
     * @param dataSourceKey データソースキー
     */
    public void set(String dataSourceKey) {
        contextHolder.set(dataSourceKey);
    }

    /**
     * データソースキーを取得する。
     * @return データソースキー
     */
    public String get() {
        return contextHolder.get();
    }

    /**
     * データソースキーを削除する。
     */
    public void clear() {
        contextHolder.remove();
    }
}
