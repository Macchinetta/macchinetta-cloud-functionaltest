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
package jp.co.ntt.cloud.functionaltest.domain.service.login;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import jp.co.ntt.cloud.functionaltest.domain.model.Account;

/**
 * 認証用ユーザ詳細情報クラス。
 * @author NTT 電電太郎
 */
public class SampleUserDetails extends User {

    /**
     * シリアルバージョンUID。
     */
    private static final long serialVersionUID = 1L;

    /**
     * ユーザ情報。
     */
    private final Account account;

    /**
     * コンストラクタ。
     * @param account ユーザ情報
     */
    public SampleUserDetails(Account account) {

        super(account.getUserId(), account.getPassword(), AuthorityUtils
                .createAuthorityList("ROLE_USER"));
        this.account = account;
    }

    /**
     * ユーザ情報を取得する。
     * @return ユーザ情報
     */
    public Account getAccount() {
        return account;
    }

}
