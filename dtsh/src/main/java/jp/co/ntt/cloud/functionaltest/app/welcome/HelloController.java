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
package jp.co.ntt.cloud.functionaltest.app.welcome;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

/**
 * Hello画面表示コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class HelloController {

    /**
     * ロガー。
     */
    private static final Logger logger = LoggerFactory.getLogger(
            HelloController.class);

    /**
     * Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.HELLO)
    public String home(Locale locale, Model model) {
        logger.info("Welcome home! The client locale is {}.", locale);

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, locale);

        String formattedDate = dateFormat.format(date);

        model.addAttribute("serverTime", formattedDate);

        return WebPagePathConstants.WELCOME_HOME;
    }

}
