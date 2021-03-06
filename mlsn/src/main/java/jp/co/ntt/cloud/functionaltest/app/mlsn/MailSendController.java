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
package jp.co.ntt.cloud.functionaltest.app.mlsn;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.service.mlsn.MailNotification;
import jp.co.ntt.cloud.functionaltest.domain.service.mlsn.SesMailSender;

/**
 * メール送信機能確認用コントローラー。
 * @author NTT 電電太郎
 */
@Controller
public class MailSendController {

    @Inject
    SesMailSender sesMailSender;

    @ModelAttribute
    public MailForm mailForm() {
        return new MailForm();
    }

    @GetMapping(value = WebPagePathConstants.ROOT_HOME)
    public String home() {
        return WebPagePathConstants.MLSN_HOME;
    }

    @PostMapping(value = WebPagePathConstants.SEND)
    public String send(MailForm mailForm, Model model) {
        MailNotification notification = null;
        if ("simple".equals(mailForm.getKind())) {
            notification = sesMailSender.registerSimple(mailForm.getTo(),
                    mailForm.getBody());
        } else if ("mime".equals(mailForm.getKind())) {
            notification = sesMailSender.registerMime(
                    "xxxx@xx.xx",
                    mailForm.getTo(), "UTF-8", "MIME Mail test", mailForm
                            .getBody());
        } else {
            throw new IllegalArgumentException("abnormal kind:" + mailForm
                    .getKind());
        }

        model.addAttribute("result", convertToMailResult(notification));

        return WebPagePathConstants.MLSN_HOME;
    }

    private MailResult convertToMailResult(MailNotification mailNotification) {
        MailResult result = new MailResult();
        result.setMessageId(mailNotification.getMessageId());
        result.setTopicArn(mailNotification.getTopicArn());
        MailNotification.Message message = mailNotification.getMessage();
        result.setNotificationType(message.getNotificationType());
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (MailNotification.Message.Mail.Header header : message.getMail()
                .getHeaders()) {
            count++;
            builder.append(header.getName()).append(":").append(header
                    .getValue());
            if (count < message.getMail().getHeaders().size()) {
                builder.append(",");
            }
        }
        result.setHeaders(builder.toString());
        return result;
    }
}
