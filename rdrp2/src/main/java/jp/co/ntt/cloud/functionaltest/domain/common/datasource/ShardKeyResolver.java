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
 * シャードキーのリゾルバー。
 * @author NTT 電電花子
 */
public interface ShardKeyResolver {

    /**
     * スキーマキー名。
     */
    String SCHEMA_KEY_NAME = "schema";

    /**
     * シャードキーを解決する。
     * @param shardKey
     * @return
     */
    String resolveShardKey(String shardKey);
}
