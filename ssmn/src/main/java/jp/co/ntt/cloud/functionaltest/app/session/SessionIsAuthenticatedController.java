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
package jp.co.ntt.cloud.functionaltest.app.session;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

/**
 * セッション試験用(認証ある場合)コントローラ
 * @author NTT 電電太郎
 */
@Controller
public class SessionIsAuthenticatedController {

    /**
     * POST 実行
     * @return
     */
    @PostMapping(value = WebPagePathConstants.SESSION_ISAUTHENTICATED, params = "postTest")
    public String post() {
        return WebPagePathConstants.SESSIONISAUTHENTICATED_SUCCESSPOST;
    }

    /**
     * GET 実行
     * @return
     */
    @GetMapping(value = WebPagePathConstants.SESSION_ISAUTHENTICATED, params = "getTest")
    public String get() {
        return WebPagePathConstants.SESSIONISAUTHENTICATED_SUCCESSGET;
    }

    /**
     * セッション試験のWelcomeページ表示
     * @return
     */
    @GetMapping(value = WebPagePathConstants.SESSION_ISAUTHENTICATED)
    public String index() {
        return WebPagePathConstants.SESSIONISAUTHENTICATED_INDEX;
    }

}
