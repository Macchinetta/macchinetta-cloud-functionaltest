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
package jp.co.ntt.cloud.functionaltest.app.reservation;

import jp.co.ntt.cloud.functionaltest.domain.model.Reservation;
import jp.co.ntt.cloud.functionaltest.domain.service.reservation.ReservationService;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller
public class ReservationController {

    @Inject
    ArrayBlockingQueue<Reservation> reservations;

    @Inject
    JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Inject
    ReservationService reservationService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("status", "init");
        return "reservation/home";
    }

    @RequestMapping(value = "/send/{message}", method = RequestMethod.GET)
    public String sendMessage(@PathVariable("message") String message,
            Model model) {
        final Reservation reservation = new Reservation();
        reservation.setReserveNo(message);
        reservationService.sendMessage(reservation);
        model.addAttribute("status", "send:" + message);
        return "reservation/home";
    }

    @RequestMapping(value = "/receive/{count}", method = RequestMethod.GET)
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
        model.addAttribute("status", builder.toString());
        return "reservation/home";
    }

    @RequestMapping(value = "/receiveSync", method = RequestMethod.GET)
    public String receiveSync(Model model) {
        Reservation reservation = reservationService.receiveMessageSync();
        model.addAttribute("status", reservation.getReserveNo());
        return "reservation/home";
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    public String clear(Model model) {
        reservations.clear();
        model.addAttribute("status", "cleared");
        return "reservation/home";
    }

    @RequestMapping(value = "/stopListener", method = RequestMethod.GET)
    public String stopListener(Model model) {
        jmsListenerEndpointRegistry.stop();
        awaitStoppingListener();
        model.addAttribute("status", "stopped");
        return "reservation/home";
    }

    @RequestMapping(value = "/startListener", method = RequestMethod.GET)
    public String startListener(Model model) {
        jmsListenerEndpointRegistry.start();
        awaitStartingListener();
        model.addAttribute("status", "started");
        return "reservation/home";
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public String messageCount(Model model) {
        model.addAttribute("status", reservations.size());
        return "reservation/home";
    }

    @RequestMapping(value = "/browse/{queueName}", method = RequestMethod.GET)
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
        model.addAttribute("status", messageId);
        return "reservation/home";
    }

    @RequestMapping(value = "/deleteAll/{queueName}", method = RequestMethod.GET)
    public String deleteAllMessages(Model model,
            @PathVariable("queueName") String queueName) {
        reservationService.deleteAllMessages(queueName);
        model.addAttribute("status", "all-cleared");
        return "reservation/home";
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
