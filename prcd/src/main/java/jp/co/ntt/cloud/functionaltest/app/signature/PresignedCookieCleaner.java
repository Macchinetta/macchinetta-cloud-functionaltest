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

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Pre-Signed Cookieを削除するクラス。
 * @author NTT 電電太郎
 */
public class PresignedCookieCleaner {

    /**
     * クラウドフロント署名ヘルパー
     */
    @Inject
    CloudFrontSignatureHelper signatureHelper;

    /**
     * Pre-Signed Cookieのキー群。
     */
    private static final String[] DELETE_COOKIES = { "CloudFront-Policy",
            "CloudFront-Signature", "CloudFront-Key-Pair-Id" };

    /**
     * 削除を実行する。
     * @param response
     */
    public void delete(HttpServletResponse response) {

        for (String cookieName : DELETE_COOKIES) {
            Cookie cookie = new Cookie(cookieName, null);
            // https://github.com/spring-projects/spring-security/issues/2325
            cookie.setPath(signatureHelper.getCookiePath() + "/");
            cookie.setDomain(signatureHelper.getDomain());
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

}
