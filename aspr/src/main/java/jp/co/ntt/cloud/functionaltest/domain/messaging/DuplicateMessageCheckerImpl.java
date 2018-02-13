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
package jp.co.ntt.cloud.functionaltest.domain.messaging;

import jp.co.ntt.cloud.functionaltest.domain.common.exception.DuplicateReceivingException;
import jp.co.ntt.cloud.functionaltest.domain.repository.messaging.MessageIdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
public class DuplicateMessageCheckerImpl implements DuplicateMessageChecker {

    private static final Logger logger = LoggerFactory.getLogger(DuplicateMessageCheckerImpl.class);

    @Inject
    MessageIdRepository repository;

    @Transactional
    @Override
    public void checkDuplicateMessage(String messageId) {
        try {
            repository.register(messageId);
            logger.debug("<<< succeeded register key [{}]", messageId);
        } catch (DuplicateKeyException e) {
            logger.warn(">>> detected duplicate key [{}]", messageId);
            throw new DuplicateReceivingException(messageId);
        }
    }
}
