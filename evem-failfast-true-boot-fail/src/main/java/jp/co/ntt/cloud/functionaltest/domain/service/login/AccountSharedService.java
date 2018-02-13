/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.domain.service.login;

import jp.co.ntt.cloud.functionaltest.domain.model.Account;

/**
 * ログイン業務共通サービスインタフェース。
 * @author NTT 電電太郎
 */
public interface AccountSharedService {

    /**
     * ユーザIDに紐づくアカウント情報を返却する。
     * @param userId ユーザID
     * @return ユーザアカウント
     */
    Account findOne(String userId);
}
