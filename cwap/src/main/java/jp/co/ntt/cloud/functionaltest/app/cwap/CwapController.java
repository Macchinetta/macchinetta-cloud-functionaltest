/*
 * Copyright(c) 2017 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.app.cwap;

import jp.co.ntt.cloud.functionaltest.app.exception.CwapCustomException;
import jp.co.ntt.cloud.functionaltest.app.filter.DuplicateCheckFilter;
import jp.co.ntt.cloud.functionaltest.app.form.CwapForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

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
    @TransactionTokenCheck(value = "cwap", type = TransactionTokenType.BEGIN)
    @RequestMapping(value = "/", method = { RequestMethod.GET,
            RequestMethod.POST })
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
        return "cwap/home";
    }

    /**
     * トランザクショントークンチェックを行う。
     * @return トークンチェック確認正常画面
     */
    @TransactionTokenCheck(value = "cwap", type = TransactionTokenType.CHECK)
    @RequestMapping(value = "/confirmToken", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String confirmToken() {
        return "cwap/tokenCheck";
    }

    /**
     * カスタムビュー表示を行う。
     * @return カスタムビュー
     */
    @RequestMapping(value = "/showCustomView")
    public String showCustomView() {
        return "cwapCustomView";
    }

    /**
     * ログ出力開始点画面の表示を行う。
     * @return ログ出力開始点画面
     */
    @RequestMapping(value = "/logging")
    public String loggingStart() {
        return "cwap/logging";
    }

    /**
     * UUIDが格納されたフォームを元に、アプリケーションログの出力を行う。
     * @param cwapForm UUID格納フォーム
     * @return ログ出力の確認を促す結果画面
     */
    @RequestMapping(value = "/outputLog", method = RequestMethod.POST)
    public String outputLog(CwapForm cwapForm) {
        logger.info("outputUUID={}", cwapForm.getUuid());
        return "cwap/logged";
    }

    /**
     * カスタム例外をハンドリングする画面を表示する。
     */
    @RequestMapping(value = "/customError")
    public void throwsCustomError() {
        throw new CwapCustomException();
    }
}
