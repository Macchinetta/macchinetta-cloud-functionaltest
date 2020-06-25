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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ntt.cloud.functionaltest.app.common.constants.CcabConstants;
import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.service.member.MemberUpdateService;

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

    @Inject
    MemberUpdateService memberUpdateService;

    /**
     * Hello画面を表示する。CacheはHeapから取得する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.HEAP)
    public String homeCacheFromHeap(Locale locale, Model model) {
        logger.info("Welcome home! The client locale is {}.", locale);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date date = new Date();

        model.addAttribute("serverTime", df.format(date));

        Member member = memberUpdateService.findMemberWithHeapCache(
                CcabConstants.CUSTOMER_NO);
        model.addAttribute("member", member);

        return WebPagePathConstants.WELCOME_HOME;
    }

    /**
     * Hello画面を表示する。CacheはRedisから取得する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.REDIS)
    public String homeCacheFromRedis(Locale locale, Model model) {
        logger.info("Welcome home! The client locale is {}.", locale);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date date = new Date();

        model.addAttribute("serverTime", df.format(date));

        Member member = memberUpdateService.findMemberWithRedisCache(
                CcabConstants.CUSTOMER_NO);
        model.addAttribute("member", member);

        return WebPagePathConstants.WELCOME_HOME;
    }

    /**
     * Heapに格納されているCache情報を削除し、メンバー情報を更新して、Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.HEAP_DELETECACHE, params = "update")
    public String deleteHeapChacheUpdate(Locale locale, Model model,
            RedirectAttributes redirectAttribute) {

        Member member = new Member();
        member.setCustomerNo(CcabConstants.CUSTOMER_NO);
        member.setName("NTT太郎");
        member.setFuriName("えぬてぃてぃたろう");
        memberUpdateService.updateMemberWithHeapCache(member);

        return WebPagePathConstants.REDIRECT_HEAP;
    }

    /**
     * Heapに格納されているCache情報を削除して、メンバー情報を初期化して、Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.HEAP_DELETECACHE, params = "redo")
    public String deleteHeapChacheRedo(Locale locale, Model model,
            RedirectAttributes redirectAttribute) {

        Member member = new Member();
        member.setCustomerNo(CcabConstants.CUSTOMER_NO);
        member.setName("電電花子");
        member.setFuriName("Hanako Denden");
        memberUpdateService.updateMemberWithHeapCache(member);

        return WebPagePathConstants.REDIRECT_HEAP;
    }

    /**
     * Redisに格納されているCache情報を削除し、メンバー情報を更新して、Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.REDIS_DELETECACHE, params = "update")
    public String deleteRedisChacheUpdate(Locale locale, Model model,
            RedirectAttributes redirectAttribute) {

        Member member = new Member();
        member.setCustomerNo(CcabConstants.CUSTOMER_NO);
        member.setName("NTT太郎");
        member.setFuriName("えぬてぃてぃたろう");
        memberUpdateService.updateMemberWithRedisCache(member);

        return WebPagePathConstants.REDIRECT_REDIS;
    }

    /**
     * Redisに格納されているCache情報を削除して、メンバー情報を初期化して、Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.REDIS_DELETECACHE, params = "redo")
    public String deleteRedisChacheRedo(Locale locale, Model model,
            RedirectAttributes redirectAttribute) {

        Member member = new Member();
        member.setCustomerNo(CcabConstants.CUSTOMER_NO);
        member.setName("電電花子");
        member.setFuriName("Hanako Denden");
        memberUpdateService.updateMemberWithRedisCache(member);

        return WebPagePathConstants.REDIRECT_REDIS;
    }

}
