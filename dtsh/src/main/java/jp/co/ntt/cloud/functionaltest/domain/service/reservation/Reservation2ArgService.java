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
package jp.co.ntt.cloud.functionaltest.domain.service.reservation;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.common.shard.annotation.ShardAccountParam;
import jp.co.ntt.cloud.functionaltest.domain.common.shard.annotation.ShardWithAccount;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.repository.reservation.ReservationRepository;

/**
 * 予約情報サービス。 {@link ShardAccountParam}を使用してシャーディングするテスト用。
 * @author NTT 電電太郎
 */
@Service
public class Reservation2ArgService {
    @Inject
    ReservationRepository reservationRespository;

    /**
     * 予約情報登録。
     * @param customerNo 会員番号
     * @param reservation 予約情報
     */
    @Transactional
    @ShardWithAccount("customerNo")
    public void register(@ShardAccountParam String customerNo,
            Reservation reservation) {
        reservationRespository.insert(reservation);
    }

    /**
     * 予約情報取得。
     * @param customerNo 会員番号
     * @param reservationNo 予約番号
     */
    @Transactional
    @ShardWithAccount("customerNo")
    public Reservation findOne(@ShardAccountParam String customerNo,
            String reservationNo) {
        return reservationRespository.findOne(reservationNo);
    }

    /**
     * 予約情報更新。。
     * @param customerNo 会員番号
     * @param reservation 予約情報
     */
    @Transactional
    @ShardWithAccount("customerNo")
    public void update(@ShardAccountParam String customerNo,
            Reservation reservation) {
        reservationRespository.update(reservation);
    }

    /**
     * 予約情報削除。
     * @param customerNo 会員番号
     * @param reservationNo 予約番号
     */
    @Transactional
    @ShardWithAccount("customerNo")
    public void delete(@ShardAccountParam String customerNo,
            String reservationNo) {
        reservationRespository.delete(reservationNo);
    }
}
