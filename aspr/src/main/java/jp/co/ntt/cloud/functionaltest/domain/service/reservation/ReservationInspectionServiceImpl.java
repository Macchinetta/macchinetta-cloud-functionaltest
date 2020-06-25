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

import jp.co.ntt.cloud.functionaltest.domain.messaging.DuplicateMessageChecker;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;

import javax.inject.Inject;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class ReservationInspectionServiceImpl implements
                                              ReservationInspectionService {

    private static final Logger logger = LoggerFactory.getLogger(
            ReservationInspectionServiceImpl.class);

    @Inject
    DuplicateMessageChecker duplicateMessageChecker;

    @Inject
    ArrayBlockingQueue<Reservation> reservationBlockingQueue;

    @Transactional
    @Override
    public void inspectAndNotify(Reservation reservation, String messageId) {

        logger.info("### checker before reserveNo[{}]", reservation
                .getReserveNo());

        duplicateMessageChecker.checkDuplicateMessage(messageId);

        if ("businessException".equalsIgnoreCase(reservation.getReserveNo())) {
            throw new BusinessException("Retryable business exception");
        }

        logger.info("### checker after reserveNo[{}]", reservation
                .getReserveNo());

        // business-process.
        reservationBlockingQueue.add(reservation);
    }
}
