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
package jp.co.ntt.cloud.functionaltest.token;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ntt.cloud.functionaltest.domain.repository.token.StoredTransactionTokenRepository;

public class TransactionTokenCleaningListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTokenCleaningListener.class);

    @Inject
    StoredTransactionTokenRepository tokenRepository;

    @EventListener
    @Transactional
    public void sessionDestroyed(HttpSessionDestroyedEvent event) {
        String sessionId = event.getSession().getId();
        try {
            tokenRepository.deleteBySessionId(sessionId);
            logger.info("Transaction tokens created by sessionId={} have been cleaned.", sessionId);
        } catch (DataAccessException e) {
            logger.warn("Failed to clean abandoned transaction tokens created by sessionId={}.", sessionId, e);
        }
    }
}
