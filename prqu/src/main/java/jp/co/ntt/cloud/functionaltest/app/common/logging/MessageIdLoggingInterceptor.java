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
package jp.co.ntt.cloud.functionaltest.app.common.logging;

import java.lang.reflect.Parameter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * メッセージIDをログ出力する為のインタセプタ。<br>
 * {@link Header}アノテーションを使用してJMSMessageIDを取得しているメソッドパラメータが存在することが前提。
 * @author NTT 電電太郎
 */
public class MessageIdLoggingInterceptor implements MethodInterceptor {

    /**
     * MDCに設定する属性名
     */
    private String attributeName = "messageId";

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // メソッドのパラメータ情報およびパラメータを取得
        Object[] arguments = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Header header = parameters[i].getAnnotation(Header.class);

            // @HeaderアノテーションのvalueにJMSMessageIDが指定されているパラメータを、MDCにputする
            if (header != null && JmsHeaders.MESSAGE_ID.equals(header.value())
                    && arguments[i] instanceof String) {
                MDC.put(this.attributeName, ((String) arguments[i]));
                break;
            }
        }

        Object ret = invocation.proceed();

        // proceed内で例外が発生した場合には、ErrorHandler側でMDC.removeを行う想定
        // 本クラスでremoveすると、ErrorHandlerでのログにmessageIdが出力されず、メッセージトレースが困難になる為
        MDC.remove(this.attributeName);

        return ret;
    }

    /**
     * 属性名を設定する。
     * @param attributeName 属性名
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}
