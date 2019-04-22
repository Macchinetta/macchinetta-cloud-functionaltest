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
package jp.co.ntt.cloud.functionaltest.app.reservation;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;

/**
 * 問い合わせ用の{@link Reservation}を作成するファクトリ
 * @author NTT 電電太郎
 */
public class ReservationFactory {
    public static Reservation create(ReservationForm reservationForm) {
        Reservation reservation = new Reservation();
        Member member = new Member();

        member.setCustomerNo(reservationForm.getCustomerNo());
        reservation.setRepMember(member);
        reservation.setReserveNo(reservationForm.getReserveNo());
        return reservation;
    }
}
