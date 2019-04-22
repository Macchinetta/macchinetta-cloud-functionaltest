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

import jp.co.ntt.cloud.functionaltest.domain.common.shard.annotation.ShardWithAccount;
import jp.co.ntt.cloud.functionaltest.domain.repository.member.MemberRepository;

/**
 * メンバー登録サービス。引数がないメソッドに{@link ShardWithAccount}アノテーションを付与して、デフォルトDBにアクセスすることを確認する。
 * @author NTT 電電太郎
 */
@Service
public class Member0ArgService {

    @Inject
    MemberRepository memberRepository;

    /**
     * メンバー情報全件削除。
     * <p>
     * 引数がないメソッドに{@link ShardWithAccount}アノテーションを付与して、デフォルトDBにアクセスすることを確認する試験用のメソッド。
     * </p>
     */
    @Transactional
    @ShardWithAccount("reservation.repMember.customerNo")
    public void deleteAll() {
        memberRepository.deleteAll();
    }
}
