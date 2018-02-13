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
package jp.co.ntt.cloud.functionaltest.app.common.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.ErrorHandler;

/**
 * エラーハンドラクラス<br>
 * <p>
 * メッセージ受信後の処理で例外が発生した場合に実行される。
 * </p>
 * @author NTT 電電太郎
 */
public class JmsErrorHandler implements ErrorHandler {

    /**
     * ロガー。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            JmsErrorHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleError(Throwable t) {

        Throwable cause = t.getCause();
        LOGGER.error(cause.getMessage(), cause);

        // MessageIdLoggingInterceptorでputされたmessageIdや、
        // MDCにputされたその他の情報をクリアする。
        MDC.clear();
    }

}
