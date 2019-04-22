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
package jp.co.ntt.cloud.functionaltest.rest.api.member;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;

@Component
public class MemberHelper {

    @Inject
    Mapper beanMapper;

    public Member resourceToMember(MemberResource memberResource) {

        Member member = beanMapper.map(memberResource, Member.class);

        LocalDate dateOfBirth = new LocalDate(memberResource
                .getYearOfBirth(), memberResource
                        .getMonthOfBirth(), memberResource.getDayOfBirth());
        member.setBirthday(dateOfBirth.toDate());

        String tel = memberResource.getTel1() + "-" + memberResource.getTel2()
                + "-" + memberResource.getTel3();
        member.setTel(tel);

        String zipCode = memberResource.getZipCode1() + memberResource
                .getZipCode2();
        member.setZipCode(zipCode);

        return member;

    }

    public MemberResource memberToResource(Member member) {

        MemberResource memberResource = beanMapper.map(member,
                MemberResource.class);

        String[] tel = member.getTel().split("-");
        if (tel.length == 3) {
            memberResource.setTel1(tel[0]);
            memberResource.setTel2(tel[1]);
            memberResource.setTel3(tel[2]);
        }

        if (StringUtils.hasLength(member.getZipCode()) && member.getZipCode()
                .length() >= 7) {
            memberResource.setZipCode1(member.getZipCode().substring(0, 3));
            memberResource.setZipCode2(member.getZipCode().substring(3, 7));
        }

        LocalDate dateOfBirth = new LocalDate(member.getBirthday());
        memberResource.setYearOfBirth(dateOfBirth.getYear());
        memberResource.setMonthOfBirth(dateOfBirth.getMonthOfYear());
        memberResource.setDayOfBirth(dateOfBirth.getDayOfMonth());

        return memberResource;
    }

}
