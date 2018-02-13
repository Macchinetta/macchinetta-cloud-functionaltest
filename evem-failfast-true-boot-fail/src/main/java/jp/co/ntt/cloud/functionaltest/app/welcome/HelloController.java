/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.app.welcome;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.ntt.cloud.functionaltest.domain.service.getconfig.GetS3ConfigService;

/**
 * Hello画面表示コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class HelloController {

    /**
     * ロガー。
     */
    private static final Logger logger = LoggerFactory
            .getLogger(HelloController.class);

    @Inject
    GetS3ConfigService getS3ConfigService;

    /**
     * Hello画面を表示する。
     *
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(value = "/", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String home(Locale locale, Model model) {
        logger.info("Welcome home! The client locale is {}.", locale);

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, locale);

        String formattedDate = dateFormat.format(date);

        model.addAttribute("serverTime", formattedDate);

        model.addAttribute("s3ConfigConfigurationPropertiesDto", getS3ConfigService.getConfigurationPropertiesDto());
        model.addAttribute("s3ConfigConfigValueDto", getS3ConfigService.getConfigurationPropertiesDto());

        return "welcome/home";
    }

}
