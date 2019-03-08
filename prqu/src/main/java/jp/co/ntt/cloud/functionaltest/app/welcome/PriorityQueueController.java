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
 *
 */
package jp.co.ntt.cloud.functionaltest.app.welcome;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.ntt.cloud.functionaltest.domain.model.FTMessage;
import jp.co.ntt.cloud.functionaltest.domain.service.login.SampleUserDetails;
import jp.co.ntt.cloud.functionaltest.domain.service.message.MessageService;
import jp.co.ntt.cloud.functionaltest.domain.service.message.PriorityQueueMessageSendService;

/**
 * 優先度キュー送信確認コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class PriorityQueueController {

    /**
     * ロガー。
     */
    private static final Logger logger = LoggerFactory.getLogger(
            PriorityQueueController.class);

    /**
     * 優先順位変更のメッセージ送信オブジェクト。
     */
    @Inject
    PriorityQueueMessageSendService priorityQueueMessageSendService;

    /**
     * メッセージサービス。
     */
    @Inject
    MessageService messageService;

    /**
     * ログインユーザのサービスレベルに応じたキューを利用してメッセージ送信を行い処理時を確認する。。
     * @param userDetails ユーザ詳細
     * @param locale 地域情報
     * @param model モデル
     * @return
     * @throws InterruptedException
     */
    @RequestMapping(value = "/", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String home(@AuthenticationPrincipal SampleUserDetails userDetails,
            Locale locale, Model model) throws InterruptedException {
        logger.info("Welcome home! The client locale is {}.", locale);

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, locale);

        String formattedDate = dateFormat.format(date);

        model.addAttribute("serverTime", formattedDate);

        // 優先順位を利用したメッセージ送信を実施
        String requestId = priorityQueueMessageSendService.sendMassage(
                userDetails.getAccount().isPremium());

        FTMessage ftMessage = null;
        for (int i = 0; i < 120; i++) {

            Thread.sleep(1000);
            ftMessage = messageService.findProcessedMessageByRequestId(
                    requestId);
            if (ftMessage != null) {
                logger.info("Functionaltest Message: {}", ftMessage);
                long processTime = (ftMessage.getProcessedAt().getTime()
                        - ftMessage.getRequestedAt().getTime()) / 1000;
                logger.info("processTime: {}", processTime);
                model.addAttribute("processTime", processTime);
                break;
            }
        }

        return "welcome/home";
    }

}
