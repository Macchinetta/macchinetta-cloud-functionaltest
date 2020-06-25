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
package jp.co.ntt.cloud.functionaltest.domain.common.shard.interceptor;

import java.util.Objects;
import java.util.Optional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

import jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.RoutingDataSourceLookUpKeyHolder;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.helper.ShardAccountHelper;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.model.ShardingAccount;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.repository.AccountShardKeyRepository;

/**
 * シャーディングのインタセプタ。
 * @author NTT 電電太郎
 */
public class AccountShardInterceptor implements MethodInterceptor,
                                     InitializingBean {

    /**
     * シャードアカウントのリポジトリー。
     */
    private AccountShardKeyRepository accountShardKeyRepository;

    private ShardAccountHelper shardAccountHelper;

    private RoutingDataSourceLookUpKeyHolder dataSourceLookupKeyHolder;

    public AccountShardInterceptor(
            AccountShardKeyRepository accountShardKeyRepository,
            ShardAccountHelper shardAccountHelper,
            RoutingDataSourceLookUpKeyHolder dataSourceLookupKeyHolder) {
        this.accountShardKeyRepository = accountShardKeyRepository;
        this.shardAccountHelper = shardAccountHelper;
        this.dataSourceLookupKeyHolder = dataSourceLookupKeyHolder;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 未使用のデフォルト実装
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // ネスト処理に対応するため、一つ前のキーを保持する
        String beforeKey = dataSourceLookupKeyHolder.get();

        String dataSourceKey = null;
        // シャードテーブルのキーを取得
        String acccount = shardAccountHelper.getAccountValue(invocation);
        if (Objects.nonNull(acccount)) {
            // リポジトリに問い合わせ
            Optional<ShardingAccount> shardingAccount = accountShardKeyRepository
                    .findById(acccount);
            if (Objects.nonNull(shardingAccount)) {
                dataSourceKey = shardingAccount.get().getDataSourceKey();
            }
        }
        dataSourceLookupKeyHolder.set(dataSourceKey);

        Object ret = null;
        try {
            ret = invocation.proceed();
        } finally {
            // データソースキーホルダクラスの状態を戻す
            if (Objects.nonNull(beforeKey)) {
                dataSourceLookupKeyHolder.set(dataSourceKey);
            } else {
                dataSourceLookupKeyHolder.clear();
            }
        }
        return ret;
    }
}
