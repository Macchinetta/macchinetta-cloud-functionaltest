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
package jp.co.ntt.cloud.functionaltest.domain.service.reservation;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Inject
    ArrayBlockingQueue<Reservation> reservations;

    @Inject
    JmsMessagingTemplate jmsMessagingTemplate;

    @Value("${aspr.sqs.visibility-timeout}")
    long visibilityTimeout;

    @Override
    public void sendMessage(Reservation reservation) {
        jmsMessagingTemplate.convertAndSend("reservation-queue", reservation);
    }

    @Override
    public Reservation receiveMessageSync() {
        return jmsMessagingTemplate.receiveAndConvert("reservation-queue", Reservation.class);
    }

    @Override
    public List<String> browseMessageIds(final String queueName, boolean delete) {
        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        final List<String> messages = new ArrayList<>();
        final ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(queueName);
        final String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
        for (com.amazonaws.services.sqs.model.Message message : receiveMessageResult.getMessages()) {
            messages.add(message.getMessageId());
            if (delete) {
                sqs.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceiptHandle()));
            }
        }
        if (!delete) {
            // メッセージ削除を実施しない場合のみ、同一スレッドの他のレシーバーが受信できなくなるため
            // 可視性タイムアウトまでスリープさせる。
            // http://docs.aws.amazon.com/ja_jp/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html
            try {
                TimeUnit.MILLISECONDS.sleep(visibilityTimeout);
            } catch (InterruptedException e) {
                // do nothing.
            }
        }
        return messages;
    }

    @Override
    public void deleteAllMessages(String queueName) {
        List<String> messages;
        do {
            messages = browseMessageIds(queueName, true);
        } while (!messages.isEmpty());
    }
}

