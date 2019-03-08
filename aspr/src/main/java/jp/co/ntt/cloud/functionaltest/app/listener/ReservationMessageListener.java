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
package jp.co.ntt.cloud.functionaltest.app.listener;

import jp.co.ntt.cloud.functionaltest.domain.common.exception.DuplicateReceivingException;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.service.reservation.ReservationInspectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ReservationMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(
            ReservationMessageListener.class);

    @Inject
    ReservationInspectionService reservationInspectionService;

    @JmsListener(destination = "reservation-queue", concurrency = "5-10")
    public void receive(Reservation reservation,
            @Header(JmsHeaders.MESSAGE_ID) String messageId) {
        try {
            reservationInspectionService.inspectAndNotify(reservation,
                    messageId);
        } catch (DuplicateReceivingException e) {
            logger.warn("*** Detected duplicate receive messageId [{}]", e
                    .getMessage());
        }
    }
}
