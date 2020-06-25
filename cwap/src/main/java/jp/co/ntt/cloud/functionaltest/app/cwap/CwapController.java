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
package jp.co.ntt.cloud.functionaltest.app.cwap;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.app.exception.CwapCustomException;
import jp.co.ntt.cloud.functionaltest.app.filter.DuplicateCheckFilter;
import jp.co.ntt.cloud.functionaltest.app.form.CwapForm;

/**
 * CWAP画面表示コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class CwapController {

    /**
     * ロガー。
     */
    private static final Logger logger = LoggerFactory.getLogger(
            CwapController.class);

    /**
     * UUIDを格納するフォームを生成する。
     * @return フォームオブジェクト
     */
    @ModelAttribute
    public CwapForm setUpForm() {
        return new CwapForm();
    }

    /**
     * Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.ROOT_HOME)
    public String home(Locale locale, Model model, HttpServletRequest request) {
        logger.info("Welcome home! The client locale is {}.", locale);

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, locale);

        String formattedDate = dateFormat.format(date);

        model.addAttribute("serverTime", formattedDate);

        AtomicInteger counter = (AtomicInteger) request.getAttribute(
                DuplicateCheckFilter.COUNTER_KEY);
        if (counter == null) {
            throw new IllegalArgumentException("counter isn't set in DuplicateCheckFilter.");
        }
        model.addAttribute("counter", counter.get());
        return WebPagePathConstants.WELCOME_HOME;
    }

    /**
     * トランザクショントークンを生成する。
     * @return トークン生成完了画面
     */
    @TransactionTokenCheck(value = "cwap", type = TransactionTokenType.BEGIN)
    @GetMapping(value = WebPagePathConstants.TRANSACTION_GENERATETOKEN)
    public String generateToken() {
        return WebPagePathConstants.TRANSACTION_GENERATETOKEN;
    }

    /**
     * トランザクショントークンチェックを行う。
     * @return トークンチェック確認正常画面
     */
    @TransactionTokenCheck(value = "cwap", type = TransactionTokenType.CHECK)
    @PostMapping(value = WebPagePathConstants.TRANSACTION_CONFIRMTOKEN)
    public String confirmToken() {
        return WebPagePathConstants.TRANSACTION_CONFIRMTOKEN;
    }

    /**
     * カスタムビュー表示を行う。
     * @return カスタムビュー
     */
    @GetMapping(value = WebPagePathConstants.SHOWCUSTOMVIEW)
    public String showCustomView() {
        return WebPagePathConstants.CWAPCUSTOMVIEW;
    }

    /**
     * ログ出力開始点画面の表示を行う。
     * @return ログ出力開始点画面
     */
    @GetMapping(value = WebPagePathConstants.LOGGER_LOGGING)
    public String loggingStart() {
        return WebPagePathConstants.LOGGER_LOGGING;
    }

    /**
     * UUIDが格納されたフォームを元に、アプリケーションログの出力を行う。
     * @param cwapForm UUID格納フォーム
     * @return ログ出力の確認を促す結果画面
     */
    @PostMapping(value = WebPagePathConstants.LOGGER_LOGGED)
    public String outputLog(CwapForm cwapForm) {
        logger.info("outputUUID={}", cwapForm.getUuid());
        return WebPagePathConstants.LOGGER_LOGGED;
    }

    /**
     * カスタム例外をハンドリングする画面を表示する。
     */
    @GetMapping(value = WebPagePathConstants.CUSTOMERROR)
    public void throwsCustomError() {
        throw new CwapCustomException();
    }
}
