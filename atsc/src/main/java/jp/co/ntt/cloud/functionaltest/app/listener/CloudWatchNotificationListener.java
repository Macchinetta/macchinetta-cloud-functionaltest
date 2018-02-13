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
package jp.co.ntt.cloud.functionaltest.app.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * CloudWatchからのしきい値超えアラーム通知リスナークラス。
 * @author NTT 電電太郎
 */
@Component
public class CloudWatchNotificationListener {

    @Inject
    @Named("messageBlockingQueue")
    ArrayBlockingQueue<String> messageBlockingQueue;

    private final Set<String> messageIdSet = new HashSet<>();

    @JmsListener(destination = "TESTAutoScaleNotification", concurrency = "5-10")
    public void receive(String message, @Header(JmsHeaders.MESSAGE_ID) String messageId) {
        synchronized (messageIdSet) {
            if (messageIdSet.contains(messageId)) {
                return;
            }
            messageIdSet.add(messageId);
        }
        try {
            messageBlockingQueue.put(message);
        } catch (InterruptedException e) {
            // do nothing.
        }
    }
}
