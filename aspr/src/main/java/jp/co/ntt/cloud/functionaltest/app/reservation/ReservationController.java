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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.ntt.cloud.functionaltest.app.common.constants.AsprConstants;
import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.service.reservation.ReservationService;

@Controller
public class ReservationController {

    @Inject
    ArrayBlockingQueue<Reservation> reservations;

    @Inject
    JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Inject
    ReservationService reservationService;

    @GetMapping(value = WebPagePathConstants.ROOT_HOME)
    public String home(Model model) {
        model.addAttribute(AsprConstants.STATUS, "init");
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.SEND_MESSAGE)
    public String sendMessage(@PathVariable("message") String message,
            Model model) {
        final Reservation reservation = new Reservation();
        reservation.setReserveNo(message);
        reservationService.sendMessage(reservation);
        model.addAttribute(AsprConstants.STATUS, "send:" + message);
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.RECEIVE_COUNT)
    public String receiveMessage(@PathVariable("count") int count,
            Model model) throws InterruptedException, TimeoutException {

        final String[] reserveNos = new String[count];
        for (int i = 0; i < count; i++) {
            final String reserveNo = reservations.poll(1L, TimeUnit.MINUTES)
                    .getReserveNo();
            if (reserveNo == null) {
                throw new TimeoutException("Can not poll message in 1 minute.");
            }
            reserveNos[i] = reserveNo;
        }

        Arrays.sort(reserveNos);

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(reserveNos[i]);
            if (i < count - 1) {
                builder.append(",");
            }
        }
        model.addAttribute(AsprConstants.STATUS, builder.toString());
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.RECEIVESYNC)
    public String receiveSync(Model model) {
        Reservation reservation = reservationService.receiveMessageSync();
        model.addAttribute(AsprConstants.STATUS, reservation.getReserveNo());
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.CLEAR)
    public String clear(Model model) {
        reservations.clear();
        model.addAttribute(AsprConstants.STATUS, "cleared");
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.STOPLISTENER)
    public String stopListener(Model model) {
        jmsListenerEndpointRegistry.stop();
        awaitStoppingListener();
        model.addAttribute(AsprConstants.STATUS, "stopped");
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.STARTLISTENER)
    public String startListener(Model model) {
        jmsListenerEndpointRegistry.start();
        awaitStartingListener();
        model.addAttribute(AsprConstants.STATUS, "started");
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.COUNT)
    public String messageCount(Model model) {
        model.addAttribute(AsprConstants.STATUS, reservations.size());
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.BROWSE_QUEUENAME)
    public String browseMessageId(Model model,
            @PathVariable("queueName") String queueName,
            @RequestParam("delete") boolean delete) {
        List<String> messageIds = reservationService.browseMessageIds(queueName,
                delete);
        String messageId;
        if (messageIds.size() > 0) {
            messageId = messageIds.get(0);
        } else {
            messageId = "EMPTY";
        }
        model.addAttribute(AsprConstants.STATUS, messageId);
        return WebPagePathConstants.RESERVATION_HOME;
    }

    @GetMapping(value = WebPagePathConstants.DELETEALL_QUEUENAME)
    public String deleteAllMessages(Model model,
            @PathVariable("queueName") String queueName) {
        reservationService.deleteAllMessages(queueName);
        model.addAttribute(AsprConstants.STATUS, "all-cleared");
        return WebPagePathConstants.RESERVATION_HOME;
    }

    private void awaitListenerState(boolean start) {
        int timeoutCount = 0;
        while (jmsListenerEndpointRegistry.isRunning() ^ start) {
            timeoutCount++;
            if (timeoutCount > 100) {
                throw new IllegalStateException("exceeded await time in JmsListener.");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void awaitStartingListener() {
        awaitListenerState(true);
    }

    private void awaitStoppingListener() {
        awaitListenerState(false);
    }
}
