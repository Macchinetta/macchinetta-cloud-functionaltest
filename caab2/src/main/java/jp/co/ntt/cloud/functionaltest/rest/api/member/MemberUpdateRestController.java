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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.service.caab.MemberUpdateService;

@RestController
@RequestMapping("Member/update")
public class MemberUpdateRestController {

    @Inject
    MemberUpdateService memberUpdateService;

    @Inject
    MemberHelper memberHelper;

    /**
     * findMember
     * <ul>
     * <li>検索条件に一致するMemberリソースの検索。</li>
     * </ul>
     */
    @RequestMapping(method = RequestMethod.GET, value = "{customerNo}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResource findMember(
            @PathVariable("customerNo") String customerNo,
            HttpServletRequest request, HttpServletResponse response) {

        request.getSession();

        Member member = memberUpdateService.findMember(customerNo);
        if (null == member) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return memberHelper.memberToResource(member);

    }

    /**
     * updateMember
     * <ul>
     * <li>特定のMemberリソースの更新。</li>
     * </ul>
     */
    @RequestMapping(method = RequestMethod.PUT, value = "{customerNo}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResource updateMember(
            @PathVariable("customerNo") String customerNo,
            @RequestBody @Validated MemberResource requestedResource,
            HttpServletResponse response) {

        Member requestedMember = memberUpdateService.findMember(customerNo);
        if (null == requestedMember) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        Member member = memberHelper.resourceToMember(requestedResource);
        member.setCustomerNo(customerNo);

        Member responseMember = memberUpdateService.updateMember(member);
        return memberHelper.memberToResource(responseMember);
    }

}
