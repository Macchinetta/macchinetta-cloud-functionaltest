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

import java.util.Random;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.repository.member.MemberRepository;

/**
 * 会員情報サービスクラス。
 * @author NTT 電電太郎
 */
@Service
@CacheConfig(cacheNames = "members")
public class MemberUpdateServiceImpl implements MemberUpdateService {

    @Inject
    MemberRepository memberRepository;

    /*
     * (non-Javadoc)
     * @see jp.co.ntt.cloud.functionaltest.domain.service.member.MemberUpdateService# findMemberWithHeapCache(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'member/' + #customerNo", cacheManager = "heapCacheManager")
    public Member findMemberWithHeapCache(String customerNo) {
        return findMember(customerNo);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.ntt.cloud.functionaltest.domain.service.member.MemberUpdateService#
     * updateMemberWithHeapCacheCache(jp.co.ntt.cloud.functionaltest.domain.model. Member)
     */
    @Override
    @Transactional
    @CacheEvict(key = "'member/' + #member.customerNo", cacheManager = "heapCacheManager")
    public void updateMemberWithHeapCache(Member member) {
        memberRepository.update(member);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.ntt.cloud.functionaltest.domain.service.member.MemberUpdateService#
     * findMemberWithRedisCacheCache(jp.co.ntt.cloud.functionaltest.domain.model. Member)
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'member/' + #customerNo", cacheManager = "redisCacheManager")
    public Member findMemberWithRedisCache(String customerNo) {
        return findMember(customerNo);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.ntt.cloud.functionaltest.domain.service.member.MemberUpdateService#
     * updateMemberWithRedisCacheCache(jp.co.ntt.cloud.functionaltest.domain.model. Member)
     */
    @Override
    @Transactional
    @CacheEvict(key = "'member/' + #member.customerNo", cacheManager = "redisCacheManager")
    public void updateMemberWithRedisCache(Member member) {
        memberRepository.update(member);
    }

    /**
     * メンバー情報を取得する。
     * @param customerNo 会員番号
     * @return メンバー情報
     */
    private Member findMember(String customerNo) {
        Member member = memberRepository.findOne(customerNo);
        member.setRandomNo(new Random().nextInt());
        return member;
    }
}
