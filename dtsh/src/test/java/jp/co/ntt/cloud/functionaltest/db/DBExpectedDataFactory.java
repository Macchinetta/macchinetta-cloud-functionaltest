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
package jp.co.ntt.cloud.functionaltest.db;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;

/**
 * @author NTT 電電太郎
 */
public class DBExpectedDataFactory {
    /**
     * 期待値となる会員情報を設定する。
     * @return 期待値となる会員情報
     */
    public static List<Member> expectedMembers() {

        List<Member> expectedMembers = new ArrayList<>();

        Member member = new Member();
        member.setCustomerNo("0000000000");
        member.setName("電電太郎");
        member.setFuriName("でんでんたろう");
        expectedMembers.add(member);

        member = new Member();
        member.setCustomerNo("0000000001");
        member.setName("電電花子");
        member.setFuriName("でんでんはなこ");
        expectedMembers.add(member);

        member = new Member();
        member.setCustomerNo("0000000002");
        member.setName("電電健太");
        member.setFuriName("でんでんけんた");
        expectedMembers.add(member);

        member = new Member();
        member.setCustomerNo("0000000003");
        member.setName("電電さおり");
        member.setFuriName("でんでんさおり");
        expectedMembers.add(member);

        return expectedMembers;
    }

    /**
     * 期待値となる会員情報を設定する。
     * @return 期待値となる会員情報
     */
    public static List<Reservation> expectedReserves() {

        List<Reservation> expectedReservations = new ArrayList<>();
        List<Member> expectedMember = expectedMembers();
        Reservation reservation = new Reservation();

        reservation.setReserveDate(new DateTime(2000, 1, 1, 12, 0));
        reservation.setReserveNo("9999999990");
        reservation.setTotalFare(10000);
        reservation.setRepMember(expectedMember.get(0));
        expectedReservations.add(reservation);

        reservation = new Reservation();
        reservation.setReserveDate(new DateTime(2010, 1, 1, 12, 0));
        reservation.setReserveNo("9999999991");
        reservation.setTotalFare(20000);
        reservation.setRepMember(expectedMember.get(1));
        expectedReservations.add(reservation);

        reservation = new Reservation();
        reservation.setReserveDate(new DateTime(2020, 1, 1, 12, 0));
        reservation.setReserveNo("9999999992");
        reservation.setTotalFare(30000);
        reservation.setRepMember(expectedMember.get(2));
        expectedReservations.add(reservation);

        reservation = new Reservation();
        reservation.setReserveDate(new DateTime(2030, 1, 1, 12, 0));
        reservation.setReserveNo("9999999993");
        reservation.setTotalFare(40000);
        reservation.setRepMember(expectedMember.get(3));
        expectedReservations.add(reservation);

        return expectedReservations;
    }
}
