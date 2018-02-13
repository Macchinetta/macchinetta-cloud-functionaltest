/*
 * Copyright 2014-2017 NTT Corporation.
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.service.member.MemberService;
import jp.co.ntt.cloud.functionaltest.domain.service.reservation.Reservation1ArgService;

/**
 * 予約情報コントローラ。
 *
 * @author NTT 電電太郎
 */
@Controller
@RequestMapping(value = "reserve/1arg")
public class Reservation1ArgController {

    @Inject
    private MemberService memberService;

    @Inject
    private Reservation1ArgService reservationService;

    /**
     * 会員登録を実施し、Hello画面を表示する。
     *
     * @param locale
     *            地域情報を保持するクラス
     * @param model
     *            出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(value = "register", method = { RequestMethod.GET, RequestMethod.POST })
    public String register() {
        new Member();
        Reservation reservation = new Reservation();

        reservation.setReserveDate(new DateTime(2000, 1, 1, 12, 0));
        reservation.setReserveNo("9999999990");
        reservation.setTotalFare(10000);
        reservation.setRepMember(memberService.findOne("0000000000"));
        reservationService.register(reservation);

        reservation.setReserveDate(new DateTime(2010, 1, 1, 12, 0));
        reservation.setReserveNo("9999999991");
        reservation.setTotalFare(20000);
        reservation.setRepMember(memberService.findOne("0000000001"));
        reservationService.register(reservation);

        reservation.setReserveDate(new DateTime(2020, 1, 1, 12, 0));
        reservation.setReserveNo("9999999992");
        reservation.setTotalFare(30000);
        reservation.setRepMember(memberService.findOne("0000000002"));
        reservationService.register(reservation);

        reservation.setReserveDate(new DateTime(2030, 1, 1, 12, 0));
        reservation.setReserveNo("9999999993");
        reservation.setTotalFare(40000);
        reservation.setRepMember(memberService.findOne("0000000003"));
        reservationService.register(reservation);

        return "redirect:/hello";
    }

    /**
     * 予約情報1件読み込み
     *
     * @return
     */
    @RequestMapping(value = "get", method = { RequestMethod.GET, RequestMethod.POST })
    public String read(Model model, RedirectAttributes redirectAttrs,
            @Valid ReservationForm reservationForm, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/hello";
        }
        Reservation reservation = reservationService.findOne(ReservationFactory.create(reservationForm));

        redirectAttrs.addFlashAttribute("reservation", reservation);

        return "redirect:/hello";
    }

    /**
     * 予約情報更新。旅行代金を0にする。
     */
    @RequestMapping(value = "update", method = { RequestMethod.GET, RequestMethod.POST })
    public String update(RedirectAttributes redirectAttrs, @Valid ReservationForm reservationForm,
            BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/hello";
        }

        Reservation reservation = reservationService.findOne(ReservationFactory.create(reservationForm));
        reservation.setTotalFare(0);
        reservationService.update(reservation);

        return "redirect:/hello";
    }

    /**
     * 予約情報削除
     */
    @RequestMapping(value = "delete", method = { RequestMethod.GET, RequestMethod.POST })
    public String delete(RedirectAttributes redirectAttrs, @Valid ReservationForm reservationForm,
            BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/hello";
        }
        reservationService.delete(ReservationFactory.create(reservationForm));
        return "redirect:/hello";
    }
}
