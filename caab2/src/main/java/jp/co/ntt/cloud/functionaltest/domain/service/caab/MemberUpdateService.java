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
package jp.co.ntt.cloud.functionaltest.domain.service.caab;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;

public interface MemberUpdateService {

    /**
     * findMember
     * <ul>
     * <li>検索条件に一致するMemberリソースの検索。</li>
     * </ul>
     */
    Member findMember(String customerNo);

    /**
     * updateMember
     * <ul>
     * <li>特定のMemberリソースの更新。</li>
     * </ul>
     */
    Member updateMember(Member member);
}
