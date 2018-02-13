/*
 * Copyright(c) 2017 NTT Corporation.
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
