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
package jp.co.ntt.cloud.functionaltest.domain.service.caab;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.repository.caab.MemberRepository;

@Service
@CacheConfig(cacheNames = "members")
public class MemberUpdateServiceImpl implements MemberUpdateService {

    @Inject
    MemberRepository memberRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'member/' + #customerNo")
    public Member findMember(String customerNo) {

        Member member = memberRepository.findOne(customerNo);
        return member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(key = "'member/' + #member.customerNo")
    public Member updateMember(Member member) {

        boolean updated = memberRepository.update(member);
        if (!updated) {
            throw new ObjectOptimisticLockingFailureException(Member.class, member
                    .getCustomerNo());
        }

        return memberRepository.findOne(member.getCustomerNo());
    }
}
