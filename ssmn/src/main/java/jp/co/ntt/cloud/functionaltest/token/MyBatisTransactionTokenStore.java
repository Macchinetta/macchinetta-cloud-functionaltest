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
 */
package jp.co.ntt.cloud.functionaltest.token;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.gfw.web.token.TokenStringGenerator;
import org.terasoluna.gfw.web.token.transaction.TransactionToken;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenStore;

import jp.co.ntt.cloud.functionaltest.domain.model.StoredTransactionToken;
import jp.co.ntt.cloud.functionaltest.domain.repository.token.StoredTransactionTokenRepository;

public class MyBatisTransactionTokenStore implements TransactionTokenStore {

    @Inject
    StoredTransactionTokenRepository tokenRepository;

    @Inject
    JodaTimeDateFactory dateFactory;

    private final int transactionTokenSizePerTokenName;

    private final TokenStringGenerator generator;

    public MyBatisTransactionTokenStore(int transactionTokenSizePerTokenName, TokenStringGenerator generator) {
        this.transactionTokenSizePerTokenName = transactionTokenSizePerTokenName;
        this.generator = generator;
    }

    public MyBatisTransactionTokenStore(int transactionTokenSizePerTokenName) {
        this(transactionTokenSizePerTokenName, new TokenStringGenerator());
    }

    public MyBatisTransactionTokenStore() {
        this(10, new TokenStringGenerator());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getAndClear(TransactionToken transactionToken) {
        String name = transactionToken.getTokenName();
        String key = transactionToken.getTokenKey();
        String sessionId = getSession().getId();

        try {
            StoredTransactionToken token = tokenRepository.findOneForUpdate(
                    name, key, sessionId);
            if (token == null) {
                return null;
            }

            tokenRepository.delete(name, key, sessionId);
            return token.getTokenValue();
        } catch (PessimisticLockingFailureException e) {
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void remove(TransactionToken transactionToken) {
        String name = transactionToken.getTokenName();
        String key = transactionToken.getTokenKey();
        String sessionId = getSession().getId();
        tokenRepository.delete(name, key, sessionId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createAndReserveTokenKey(String tokenName) {
        String sessionId = getSession().getId();
        tokenRepository.deleteOlderThanNLatest(tokenName, sessionId, transactionTokenSizePerTokenName - 1);
        return generator.generate(UUID.randomUUID().toString());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void store(TransactionToken transactionToken) {
        StoredTransactionToken token = new StoredTransactionToken();
        token.setTokenName(transactionToken.getTokenName());
        token.setTokenKey(transactionToken.getTokenKey());
        token.setTokenValue(transactionToken.getTokenValue());
        token.setSessionId(getSession().getId());
        tokenRepository.insert(token);

        getSession();
    }

    HttpSession getSession() {
        return getRequest().getSession(true);
    }

    HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
    }
}
