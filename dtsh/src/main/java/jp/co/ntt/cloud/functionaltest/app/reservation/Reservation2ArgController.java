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
package jp.co.ntt.cloud.functionaltest.app.reservation;

import javax.inject.Inject;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.service.member.MemberService;
import jp.co.ntt.cloud.functionaltest.domain.service.reservation.Reservation2ArgService;

/**
 * 予約情報コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class Reservation2ArgController {

    @Inject
    private MemberService memberService;

    @Inject
    private Reservation2ArgService reservation2ArgService;

    /**
     * 会員登録を実施し、Hello画面を表示する。
     * @param locale 地域情報を保持するクラス
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.RESERVE_2ARG_REGISTER)
    public String register() {
        new Member();
        Reservation reservation = new Reservation();

        reservation.setReserveDate(new DateTime(2000, 1, 1, 12, 0));
        reservation.setReserveNo("9999999990");
        reservation.setTotalFare(10000);
        reservation.setRepMember(memberService.findOne("0000000000"));
        reservation2ArgService.register(reservation.getRepMember()
                .getCustomerNo(), reservation);

        reservation.setReserveDate(new DateTime(2010, 1, 1, 12, 0));
        reservation.setReserveNo("9999999991");
        reservation.setTotalFare(20000);
        reservation.setRepMember(memberService.findOne("0000000001"));
        reservation2ArgService.register(reservation.getRepMember()
                .getCustomerNo(), reservation);

        reservation.setReserveDate(new DateTime(2020, 1, 1, 12, 0));
        reservation.setReserveNo("9999999992");
        reservation.setTotalFare(30000);
        reservation.setRepMember(memberService.findOne("0000000002"));
        reservation2ArgService.register(reservation.getRepMember()
                .getCustomerNo(), reservation);

        reservation.setReserveDate(new DateTime(2030, 1, 1, 12, 0));
        reservation.setReserveNo("9999999993");
        reservation.setTotalFare(40000);
        reservation.setRepMember(memberService.findOne("0000000003"));
        reservation2ArgService.register(reservation.getRepMember()
                .getCustomerNo(), reservation);

        return WebPagePathConstants.REDIRECT_HELLO;
    }

    /**
     * 予約情報1件読み込み
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.RESERVE_2ARG_GET)
    public String read(Model model, RedirectAttributes redirectAttrs,
            @Valid ReservationForm reservationForm, BindingResult result) {
        if (result.hasErrors()) {
            return WebPagePathConstants.REDIRECT_ROOT_HOME;
        }

        Reservation reservation = reservation2ArgService.findOne(reservationForm
                .getCustomerNo(), reservationForm.getReserveNo());

        redirectAttrs.addFlashAttribute("reservation", reservation);

        return WebPagePathConstants.REDIRECT_HELLO;
    }

    /**
     * 予約情報更新。旅行代金を0にする。
     */
    @GetMapping(value = WebPagePathConstants.RESERVE_2ARG_UPDATE)
    public String update(RedirectAttributes redirectAttrs,
            @Valid ReservationForm reservationForm, BindingResult result) {
        Reservation reservation = reservation2ArgService.findOne(reservationForm
                .getCustomerNo(), reservationForm.getReserveNo());
        if (result.hasErrors()) {
            return WebPagePathConstants.REDIRECT_HELLO;
        }

        reservation.setTotalFare(0);
        reservation2ArgService.update(reservation.getRepMember()
                .getCustomerNo(), reservation);

        return WebPagePathConstants.REDIRECT_HELLO;
    }

    /**
     * 予約情報削除
     */
    @GetMapping(value = WebPagePathConstants.RESERVE_2ARG_DELETE)
    public String delete(RedirectAttributes redirectAttrs,
            @Valid ReservationForm reservationForm, BindingResult result) {
        if (result.hasErrors()) {
            return WebPagePathConstants.REDIRECT_HELLO;
        }

        reservation2ArgService.delete(reservationForm.getCustomerNo(),
                reservationForm.getReserveNo());
        return WebPagePathConstants.REDIRECT_HELLO;
    }
}
