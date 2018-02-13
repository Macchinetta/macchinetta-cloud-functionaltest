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
package jp.co.ntt.cloud.functionaltest.domain.service.message;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import jp.co.ntt.cloud.functionaltest.domain.model.FTMessage;

/**
 * 優先順位を使用したメッセージ送信クラス。
 * @author NTT 電電太郎
 */
@Service
public class PriorityQueueMessageSendServiceImpl implements
                                                 PriorityQueueMessageSendService {

    /**
     * JMS送信テンプレート
     */
    @Inject
    JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 優先度高のキュー名。
     */
    @Value("${app.priority.queue.high.name}")
    private String highPriorityQueueName;

    /**
     * 優先度低のキュー名。
     */
    @Value("${app.priority.queue.low.name}")
    private String lowPriorityQueueName;


    /**
     * {@inheritDoc}
     */
    @Override
    public String sendMassage(boolean premium) {
        String requestId = UUID.randomUUID().toString();
        if (premium) {
            jmsMessagingTemplate.convertAndSend(highPriorityQueueName,
                    new FTMessage("send message with high priority queue", requestId));
        } else {
            jmsMessagingTemplate.convertAndSend(lowPriorityQueueName,
                    new FTMessage("send message with low priority queue", requestId));
        }
        return requestId;

    }

}
