/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.domain.service.login;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import jp.co.ntt.cloud.functionaltest.domain.model.Account;
import jp.co.ntt.cloud.functionaltest.domain.repository.login.AccountRepository;

/**
 * ログイン業務共通サービス実装クラス。
 * @author NTT 電電太郎
 */
@Service
public class AccountSharedServiceImpl implements AccountSharedService {

    /**
     * ユーザアカウントリポジトリ。
     */
    @Inject
    AccountRepository accountRepository;

    /**
     * ユーザIDに紐づくアカウント情報を返却する。
     * @param userId ユーザID
     * @return ユーザアカウント
     */
    @Transactional(readOnly = true)
    public Account findOne(String username) {

        Account account = accountRepository.findOne(username);

        if (account == null) {
            throw new ResourceNotFoundException("The given account is not found! username="
                    + username);
        }
        return account;
    }

}
