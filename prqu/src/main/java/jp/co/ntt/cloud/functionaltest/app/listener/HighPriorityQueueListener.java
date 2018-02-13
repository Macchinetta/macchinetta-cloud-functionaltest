/*
 * Copyright 2014-2017 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.app.listener;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import jp.co.ntt.cloud.functionaltest.app.common.handler.JmsErrorHandler;
import jp.co.ntt.cloud.functionaltest.domain.common.exception.DuplicateReceivingException;
import jp.co.ntt.cloud.functionaltest.domain.common.logging.LogMessages;
import jp.co.ntt.cloud.functionaltest.domain.model.FTMessage;
import jp.co.ntt.cloud.functionaltest.domain.service.message.MessageService;

/**
 * チケット予約情報のメッセージを受信するクラス。
 * @author NTT 電電太郎
 */
@Component
@Profile("high-priority-queue")
public class HighPriorityQueueListener {

    /**
     * ロガー。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            JmsErrorHandler.class);

    /**
     * メッセージサービス
     */
    @Inject
    MessageService messageService;

    /**
     * キュー名
     */
    @Value("${app.priority.queue.high.name}")
    String queueName;


    /**
     * キューからメッセージを受信し、フロントとの非同期処理を行う
     * @param ftMessage メッセージオブジェクト
     * @param messageId メッセージID
     */
    @JmsListener(destination = "${app.priority.queue.high.name}", concurrency = "${app.priority.queue.high.concurrency}")
    public void receive(FTMessage ftMessage,
            @Header(JmsHeaders.MESSAGE_ID) String messageId) {

        try {
            // メッセージ処理を実施
            LOGGER.info(LogMessages.I_FT_PQ_L0001.getMessage(queueName,
                    messageId));
            messageService.processMessage(ftMessage, messageId,
                    queueName);
        } catch (DuplicateReceivingException e) {
            // 2重受信の場合は処理をスキップする。
            return;
        } finally {
            LOGGER.info(LogMessages.I_FT_PQ_L0002.getMessage(queueName,
                    messageId));
        }
    }



}
