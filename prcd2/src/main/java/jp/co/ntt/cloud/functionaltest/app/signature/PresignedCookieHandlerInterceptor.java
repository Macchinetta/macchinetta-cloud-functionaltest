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
package jp.co.ntt.cloud.functionaltest.app.signature;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner.CookiesForCustomPolicy;

/**
 * {@link presignedCookie}が付与されているコントローラメソッドが呼ばれた場合にPre-Signed Cookieを発行するハンドラーインターセプタ。
 * @author NTT 電電太郎
 */
public class PresignedCookieHandlerInterceptor implements HandlerInterceptor {

    /**
     * クラウドフロント署名ヘルパー
     */
    @Inject
    CloudFrontSignatureHelper signatureHelper;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        // omitted
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        if (!enablePresignedCookie(handler)) {
            return;
        }

        // 署名発行処理
        CookiesForCustomPolicy cookies = signatureHelper.getSignedCookies();

        Cookie cookiePolicy = new Cookie(cookies.getPolicy().getKey(), cookies
                .getPolicy().getValue());
        cookiePolicy.setHttpOnly(true);
        cookiePolicy.setSecure(signatureHelper.isSecure());
        cookiePolicy.setDomain(signatureHelper.getDomain());
        cookiePolicy.setPath(signatureHelper.getCookiePath());
        response.addCookie(cookiePolicy);

        Cookie cookieSignature = new Cookie(cookies.getSignature()
                .getKey(), cookies.getSignature().getValue());
        cookieSignature.setHttpOnly(true);
        cookieSignature.setSecure(signatureHelper.isSecure());
        cookieSignature.setDomain(signatureHelper.getDomain());
        cookieSignature.setPath(signatureHelper.getCookiePath());
        response.addCookie(cookieSignature);

        Cookie cookieKeyPairId = new Cookie(cookies.getKeyPairId()
                .getKey(), cookies.getKeyPairId().getValue());
        cookieKeyPairId.setHttpOnly(true);
        cookieKeyPairId.setSecure(signatureHelper.isSecure());
        cookieKeyPairId.setDomain(signatureHelper.getDomain());
        cookieKeyPairId.setPath(signatureHelper.getCookiePath());
        response.addCookie(cookieKeyPairId);
    }


    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            Exception ex) throws Exception {

        // omitted

    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities();
        }
        return null;
    }

    private boolean enablePresignedCookie(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        PresignedCookie presignedCookie = HandlerMethod.class.cast(handler)
                .getMethodAnnotation(PresignedCookie.class);

        if (presignedCookie == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        if (authorities == null) {
            return false;
        }

        for (String role : presignedCookie.value()) {
            if (authorities.toString().contains(role)) {
                return true;
            }
        }

        return false;
    }

}
