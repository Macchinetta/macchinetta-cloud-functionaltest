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
package jp.co.ntt.cloud.functionaltest.domain.repository.reservation;

import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;

/**
 * 予約テーブルにアクセスするリポジトリインターフェース。
 * @author NTT 電電太郎
 */
public interface ReservationRepository {

    /**
     * 予約情報を登録する。
     * @param reservation 予約情報
     * @return 登録件数
     */
    int insert(Reservation reservation);

    /**
     * 予約情報を取得する。
     * @param reserveNo 予約番号
     * @return 予約情報
     */
    Reservation findOne(String reserveNo);

    /**
     * 予約情報を更新する。
     * @param reservation 予約情報
     * @return 更新件数
     */
    int update(Reservation reservation);

    /**
     * 予約情報を削除する。
     * @param reservation 予約番号
     * @return 削除件数
     */
    int delete(String reservationNo);

}
