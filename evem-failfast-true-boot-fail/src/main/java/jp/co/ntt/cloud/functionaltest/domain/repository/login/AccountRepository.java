/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.domain.repository.login;

import jp.co.ntt.cloud.functionaltest.domain.model.Account;

/**
 * アカウントテーブルにアクセスするリポジトリインターフェース
 * @author NTT 電電太郎
 */
public interface AccountRepository {

    /**
     * アカウント情報を取得する。
     * @param userId ユーザID
     * @return アカウント情報
     */
    Account findOne(String userId);
}
