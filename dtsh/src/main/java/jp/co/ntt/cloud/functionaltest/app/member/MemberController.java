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
package jp.co.ntt.cloud.functionaltest.app.member;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.service.member.Member0ArgService;
import jp.co.ntt.cloud.functionaltest.domain.service.member.MemberService;

/**
 * 会員情報コントローラ
 * @author NTT 電電太郎
 */
@Controller
@RequestMapping(value = "member")
public class MemberController {
    @Inject
    private MemberService memberService;

    @Inject
    private Member0ArgService member0ArgService;

    /**
     * 会員登録を実施し、Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(value = "register", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String register() {
        Member member = new Member();

        member.setCustomerNo("0000000000");
        member.setName("電電太郎");
        member.setFuriName("でんでんたろう");
        memberService.register(member);

        member.setCustomerNo("0000000001");
        member.setName("電電花子");
        member.setFuriName("でんでんはなこ");
        memberService.register(member);

        member.setCustomerNo("0000000002");
        member.setName("電電健太");
        member.setFuriName("でんでんけんた");
        memberService.register(member);

        member.setCustomerNo("0000000003");
        member.setName("電電さおり");
        member.setFuriName("でんでんさおり");
        memberService.register(member);

        return "redirect:/hello";
    }

    /**
     * 会員情報1件読み込み
     * @return
     */
    @RequestMapping(value = "get", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String read(Model model, RedirectAttributes redirectAttrs,
            @Valid MemberForm memberForm, BindingResult result) {

        if (result.hasErrors()) {
            return "redirect:/";
        }
        Member member = memberService.findOne(memberForm.getCustomerNo());

        redirectAttrs.addFlashAttribute("member", member);

        return "redirect:/hello";
    }

    /**
     * 会員情報更新
     */
    @RequestMapping(value = "update", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String update(Model model, RedirectAttributes redirectAttrs,
            @Valid MemberForm memberForm, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/";
        }
        Member member = memberService.findOne(memberForm.getCustomerNo());
        member.setFuriName("-");
        memberService.update(member);
        return "redirect:/hello";
    }

    /**
     * 会員情報削除
     */
    @RequestMapping(value = "delete", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String delete(Model model, @Valid MemberForm memberForm,
            BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/hello";
        }
        memberService.delete(memberForm.getCustomerNo());
        return "redirect:/hello";
    }

    /**
     * 会員情報全件削除
     */
    @RequestMapping(value = "delete", params = "all", method = {
            RequestMethod.GET, RequestMethod.POST })
    public String deleteAll(Model model) {
        member0ArgService.deleteAll();
        return "redirect:/hello";
    }
}
