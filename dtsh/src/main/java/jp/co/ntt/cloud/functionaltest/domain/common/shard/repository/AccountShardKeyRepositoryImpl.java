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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.model.ShardingAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountShardKeyRepositoryImpl implements
                                           AccountShardKeyRepository {

    private final AmazonDynamoDB amazonDynamoDB;

    private static final String DYNAMODB_TABLENAME = "FuncShardAccount";

    private static final String DYNAMODB_PRIMARYKEY = "user_id";

    private static final String DYNAMODB_ATTRIBUTE = "data_source_key";

    public AccountShardKeyRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
        this.amazonDynamoDB = amazonDynamoDB;
    }

    @Override
    public Optional<ShardingAccount> findById(String id) {
        final ShardingAccount shardingAccount = new ShardingAccount();
        shardingAccount.setId(id);

        final Map<String, AttributeValue> primaryKey = new HashMap<>();
        primaryKey.put(DYNAMODB_PRIMARYKEY, new AttributeValue().withS(id));
        final GetItemRequest request = new GetItemRequest().withTableName(
                DYNAMODB_TABLENAME).withKey(primaryKey);

        final GetItemResult item = amazonDynamoDB.getItem(request);
        shardingAccount.setDataSourceKey(item.getItem().get(DYNAMODB_ATTRIBUTE)
                .getS());

        return Optional.of(shardingAccount);
    }

    @Override
    public void save(ShardingAccount shardingAccount) {

        final PutItemRequest request = new PutItemRequest().withTableName(
                DYNAMODB_TABLENAME).addItemEntry(DYNAMODB_PRIMARYKEY,
                        new AttributeValue(shardingAccount.getId()))
                .addItemEntry(DYNAMODB_ATTRIBUTE,
                        new AttributeValue(shardingAccount.getDataSourceKey()));

        amazonDynamoDB.putItem(request);
    }
}
