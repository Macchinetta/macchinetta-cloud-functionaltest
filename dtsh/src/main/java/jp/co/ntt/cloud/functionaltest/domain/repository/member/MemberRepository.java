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
package jp.co.ntt.cloud.functionaltest.domain.repository.member;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;

/**
 * 会員情報テーブルにアクセスするリポジトリインターフェース。
 * @author NTT 電電太郎
 */
public interface MemberRepository {

    /**
     * 会員情報を登録する。
     * <p>
     * 登録後の際に、DBのシーケンスより発出されたcustomerNoが引数のmemberに格納される。
     * </p>
     * @param member 登録する会員情報
     * @return 登録件数
     */
    int insert(Member member);

    /**
     * 会員情報を1件取得する。
     * @param customerNo 会員番号
     * @return 会員情報
     */
    Member findOne(String customerNo);

    /**
     * 会員情報を削除する。
     * @param customerNo 会員番号
     * @return 会員情報
     */
    int delete(String customerNo);

    /**
     * 会員情報を更新する
     * @param customerNo 会員番号
     */
    int update(Member member);

    /**
     * 会員情報を全件削除する。
     */
    void deleteAll();

}
