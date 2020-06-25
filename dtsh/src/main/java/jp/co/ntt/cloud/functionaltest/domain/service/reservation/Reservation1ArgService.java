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
package jp.co.ntt.cloud.functionaltest.domain.service.reservation;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.common.shard.annotation.ShardWithAccount;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.repository.reservation.ReservationRepository;

/**
 * 予約情報サービス。シャーディングテスト用。
 * @author NTT 電電太郎
 */
@Service
public class Reservation1ArgService {

    @Inject
    ReservationRepository reservationRespository;

    /**
     * 予約情報登録。
     * @param reservation 予約情報
     */
    @Transactional
    @ShardWithAccount("reservation.repMember.customerNo")
    public void register(Reservation reservation) {
        reservationRespository.insert(reservation);
    }

    /**
     * 予約情報取得。
     * @param reservation 予約情報
     */
    @Transactional
    @ShardWithAccount("reservation.repMember.customerNo")
    public Reservation findOne(Reservation reservation) {
        return reservationRespository.findOne(reservation.getReserveNo());
    }

    /**
     * 予約情報更新。
     * @param reservation 予約情報
     */
    @Transactional
    @ShardWithAccount("reservation.repMember.customerNo")
    public void update(Reservation reservation) {
        reservationRespository.update(reservation);
    }

    /**
     * 予約情報削除。
     * @param reservation 予約情報
     */
    @Transactional
    @ShardWithAccount("reservation.repMember.customerNo")
    public void delete(Reservation reservation) {
        reservationRespository.delete(reservation.getReserveNo());
    }
}
