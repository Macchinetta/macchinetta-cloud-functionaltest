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
package jp.co.ntt.cloud.functionaltest.domain.common;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.common.exception.DuplicateReceivingException;
import jp.co.ntt.cloud.functionaltest.domain.repository.messaging.MessageIdRepository;

/**
 * メッセージの2重受信検出を行うクラス。
 * @author NTT 電電太郎
 */
public class DuplicateMessageChecker {

    /**
     * ロガー。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            DuplicateMessageChecker.class);

    /**
     * メッセージIDテーブルリポジトリ。
     */
    @Inject
    MessageIdRepository repository;

    /**
     * メッセージIDテーブルへのメッセージID挿入によって、2重受信のチェックを行う。
     * @throws DuplicateReceivingException 2重受信発生の場合
     */
    @Transactional
    public void checkDuplicateMessage(String queueName, String messageId) {

        // メッセージIDの重複をチェック
        // 一意性制約違反が発生した場合は2重受信
        try {
            repository.register(queueName, messageId);
        } catch (DuplicateKeyException e) {
            LOGGER.debug("duplicate message is received messageId: {}",
                    messageId);
            throw new DuplicateReceivingException(messageId);
        }
    }

}
