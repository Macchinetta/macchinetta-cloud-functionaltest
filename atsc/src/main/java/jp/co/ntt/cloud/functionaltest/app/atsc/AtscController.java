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
package jp.co.ntt.cloud.functionaltest.app.atsc;

import jp.co.ntt.cloud.functionaltest.domain.service.NotificationParseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * オートスケーリング確認用コントローラクラス。
 * @author NTT 電電太郎
 */
@Controller
public class AtscController {

    @Inject
    @Named("messageBlockingQueue")
    ArrayBlockingQueue<String> messageQueue;

    @Inject
    NotificationParseService notificationParseService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "atsc/home";
    }

    @RequestMapping(value = "/listen", method = RequestMethod.POST)
    public String listen(Model model) throws InterruptedException {
        String notification = messageQueue.poll(3L, TimeUnit.MINUTES);
        if (notification == null) {
            throw new IllegalStateException("Timeout: notification is empty.");
        }
        model.addAttribute("notification", notificationParseService
                .parseNotification(notification));
        return "atsc/home";
    }
}
