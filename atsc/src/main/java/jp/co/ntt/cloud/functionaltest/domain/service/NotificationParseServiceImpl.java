/*
 * Copyright 2014-2020 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.domain.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * アラーム通知JSON解析サービス。
 * @author NTT 電電太郎
 */
@Component
public class NotificationParseServiceImpl implements NotificationParseService {

    @Override
    public Notification parseNotification(String notificationStr) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        Notification notification;
        try {
            notification = mapper.readValue(notificationStr,
                    Notification.class);
            String messageStr = notification.getMessageStr();
            Notification.Message message = mapper.readValue(messageStr,
                    Notification.Message.class);
            notification.setMessage(message);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON mapping failed.", e);
        }
        return notification;
    }
}
