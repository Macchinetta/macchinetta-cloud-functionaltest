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
 */
package jp.co.ntt.cloud.functionaltest.domain.common.shard.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import jp.co.ntt.cloud.functionaltest.domain.common.shard.model.ShardingAccount;

/**
 * 　シャードアカウントのリポジトリー。
 * @author NTT 電電太郎
 */
@CacheConfig(cacheNames = "shardids")
@EnableScan
public interface AccountShardKeyRepository
                                          extends
                                          CrudRepository<ShardingAccount, String> {

    /*
     * (非 Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
     */
    @Override
    @Cacheable(key = "'shardid/' + #a0")
    ShardingAccount findOne(String id);

}
