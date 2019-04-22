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
package jp.co.ntt.cloud.functionaltest.domain.service.member;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.common.shard.datasource.ShardKeyResolver;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.model.ShardingAccount;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.repository.AccountShardKeyRepository;
import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.repository.member.MemberRepository;

/**
 * メンバー登録サービス
 * @author NTT 電電太郎
 */
@Service
public class MemberService {

    @Inject
    private ShardKeyResolver shardKeyResolver;

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private AccountShardKeyRepository accountShardKeyRepository;

    /**
     * メンバーを登録する。
     * @param member
     * @return
     */
    @Transactional
    public Member register(Member member) {
        memberRepository.insert(member);

        ShardingAccount shardingAccount = new ShardingAccount();
        shardingAccount.setUserId(member.getCustomerNo());
        shardingAccount.setDataSourceKey(shardKeyResolver.resolveShardKey(
                Integer.parseInt(member.getCustomerNo())));

        // シャードのマッピング情報をKVSに登録する。
        accountShardKeyRepository.save(shardingAccount);

        return member;
    }

    /**
     * メンバーを1件取得する
     * @param customerNo 会員番号
     * @return 会員情報
     */
    @Transactional(readOnly = true)
    public Member findOne(String customerNo) {
        return memberRepository.findOne(customerNo);
    }

    /**
     * 会員情報を更新する。
     * @param member 会員情報
     */
    @Transactional
    public void update(Member member) {
        memberRepository.update(member);
    }

    /**
     * 会員情報を削除する
     * @param customerNo 会員番号
     */
    @Transactional
    public void delete(String customerNo) {
        memberRepository.delete(customerNo);
    }
}
