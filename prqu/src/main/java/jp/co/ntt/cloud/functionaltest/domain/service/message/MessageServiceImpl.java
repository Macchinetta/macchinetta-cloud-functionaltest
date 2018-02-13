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
package jp.co.ntt.cloud.functionaltest.domain.service.message;

import java.util.Date;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.common.DuplicateMessageChecker;
import jp.co.ntt.cloud.functionaltest.domain.model.FTMessage;
import jp.co.ntt.cloud.functionaltest.domain.repository.messaging.FTMessageRepository;

/**
 * メッセージサービスクラス。
 * @author NTT 電電太郎
 */
@Service
public class MessageServiceImpl implements MessageService {

    /**
     * 2重受信チェックユーティリティ
     */
    @Inject
    DuplicateMessageChecker duplicateMessageChecker;

    /**
     * ファンクショナルテストリポジトリ。
     */
    @Inject
    FTMessageRepository ftMessageRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void processMessage(FTMessage ftMessage, String messageId,
            String queueName) {
        // 2重受信チェックを行う
        duplicateMessageChecker.checkDuplicateMessage(queueName, messageId);
        ftMessage.setProcessedAt(new Date());
        ftMessageRepository.register(ftMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FTMessage findProcessedMessageByRequestId(String requestId) {
        return ftMessageRepository.findOne(requestId);
    }

}
