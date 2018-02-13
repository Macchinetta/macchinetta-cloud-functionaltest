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
package jp.co.ntt.cloud.functionaltest.app.common.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * tilesのincludeを使用した際にセッションがうまくセットできない問題への暫定対処を行うフィルター。
 *
 * @author NTT 電電太郎
 *
 */
// FIXME tilesのincludeを使用した際にセッションがうまくセットできない問題への暫定対処
// ( issue : https://github.com/spring-projects/spring-session/issues/571 )
public class SessionEnforcerFilter extends OncePerRequestFilter {

    /**
     * ロガー
     */
    private static final Logger logger = LoggerFactory.getLogger(SessionEnforcerFilter.class);

    /**
     * {@link SessionEnforcerFilter}を適用しないパスを保持するマッチャー。
     */
    private RequestMatcher excludeRequestMatcher;

    /**
     * {@link SessionEnforcerFilter}を適用しないパスを設定する。
     *
     * @param excludeRequestMatcher
     *            {@link SessionEnforcerFilter}を適用しないパス
     */
    public void setExcludeRequestMatcher(RequestMatcher excludeRequestMatcher) {
        this.excludeRequestMatcher = excludeRequestMatcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest httpServletRequest = request;
        HttpServletResponse httpServletResponse = response;

        // セッションを利用しなくて良いようなパスが設定されている場合
        if (this.excludeRequestMatcher != null && this.excludeRequestMatcher.matches(httpServletRequest)) {
            chain.doFilter(httpServletRequest, response);
            return;
        }

        if (httpServletRequest.getRequestedSessionId() == null
                && httpServletRequest.getMethod().toUpperCase().equals("GET")) {

            // セッションなしでGETのリクエスト:セッションを作成して、リダイレクト
            if (logger.isDebugEnabled()) {
                logger.debug("sessionEnforcerFilter.doFilter() - Session is null - forcing its creation.");
            }
            httpServletRequest.getSession();

            StringBuilder requestURI = new StringBuilder(httpServletRequest.getRequestURI());
            if (httpServletRequest.getQueryString() != null) {
                requestURI.append("?").append(httpServletRequest.getQueryString());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("sessionEnforcerFilter.doFilter() - Repatin request [{}]", requestURI);
            }
            httpServletResponse.sendRedirect(requestURI.toString());

        } else {
            chain.doFilter(httpServletRequest, response);
        }

    }
}
