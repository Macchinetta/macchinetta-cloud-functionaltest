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
package jp.co.ntt.cloud.functionaltest.domain.service.mlsn;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Amazon SESによるメール送信機能。
 * @author NTT 電電太郎
 */
@Component
public class SesMailSenderImpl implements SesMailSender {

    @Inject
    JavaMailSender mailSender;

    @Inject
    SimpleMailMessage templateMessage;

    @Inject
    @Named("receiveQueue")
    ArrayBlockingQueue<String> receiveQueue;

    @Override
    public MailNotification registerSimple(String to, String body) {
        // for previous delay receive.
        receiveQueue.clear();

        final SimpleMailMessage message = new SimpleMailMessage(templateMessage);
        message.setTo(to);
        message.setText(body);
        mailSender.send(message);

        return receiveMessage();
    }

    @Override
    public MailNotification registerMime(final String from, final String to, final String charset,
            String subject, String body) {
        // for previous delay receive.
        receiveQueue.clear();

        mailSender.send(new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                        charset);
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body);
            }
        });

        return receiveMessage();
    }

    private MailNotification receiveMessage() {
        String src = null;
        try {
            src = receiveQueue.poll(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // Ignore.
        }
        if (src == null) {
            throw new IllegalStateException("Receive timeout exceeded");
        }
        return convertToNotification(src);
    }

    private MailNotification convertToNotification(String src) {
        final ObjectMapper mapper = new ObjectMapper();
        MailNotification notification = null;
        try {
            notification = mapper.readValue(src, MailNotification.class);
            // because of returning String literal(NOT JSON) from AWS.
            notification.setMessage(mapper.readValue(
                    notification.getMessageStr(),
                    MailNotification.Message.class));
        } catch (IOException e) {
            throw new IllegalStateException("Invalid JSON Format.", e);
        }
        return notification;
    }
}
