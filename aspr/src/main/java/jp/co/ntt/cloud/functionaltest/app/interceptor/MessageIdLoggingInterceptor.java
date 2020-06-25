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
package jp.co.ntt.cloud.functionaltest.app.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.lang.reflect.Parameter;

public class MessageIdLoggingInterceptor implements MethodInterceptor {

    private static final String KEY = "messageId";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object[] arguments = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Header header = parameters[i].getAnnotation(Header.class);

            if (header != null && JmsHeaders.MESSAGE_ID.equals(header.value())
                    && arguments[i] instanceof String) {
                MDC.put(KEY, ((String) arguments[i]));
                break;
            }
        }

        Object ret = invocation.proceed();

        MDC.remove(KEY);

        return ret;
    }
}
