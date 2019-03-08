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
package jp.co.ntt.cloud.functionaltest.app.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring BootとBean定義ファイル内の多重定義抑止確認用のサーブレットフィルタ。
 * @author NTT 電電太郎
 */
public class DuplicateCheckFilter implements Filter {

    public static final String COUNTER_KEY = DuplicateCheckFilter.class
            .getName() + ".COUNTER_KEY";

    private static final Logger logger = LoggerFactory.getLogger(
            DuplicateCheckFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        AtomicInteger counter = (AtomicInteger) request.getAttribute(
                COUNTER_KEY);
        if (counter == null) {
            counter = new AtomicInteger(0);
            request.setAttribute(COUNTER_KEY, counter);
        }
        int current = counter.incrementAndGet();
        logger.info("now counter value[{}]", current);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Do nothing.
    }
}
