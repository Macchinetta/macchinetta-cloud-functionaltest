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
package jp.co.ntt.cloud.functionaltest.domain.repository.member;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;

/**
 * メンバーテーブルにアクセスするリポジトリインターフェース
 *
 * @author NTT 電電太郎
 *
 */
public interface MemberRepository {

    /**
     * メンバー情報を取得する。
     *
     * @param customerId
     *            お客様番号
     * @return メンバー情報
     */
    Member findOne(String customerNo);

    /**
     * メンバー情報を更新する。
     *
     * @param メンバー情報
     */
    void update(Member member);
}
