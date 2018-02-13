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
package jp.co.ntt.cloud.functionaltest.app.session;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * セッション試験用(認証なし場合)コントローラ
 *
 * @author NTT 電電太郎
 *
 */
@Controller
@RequestMapping("session/noAuthenticated")
public class SessionNoAuthenticatedController {

    /**
     * POST 実行
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "postTest")
    public String post() {
        return "sessionNoAuthenticated/successPost";
    }

    /**
     * GET 実行
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = "getTest")
    public String get() {
        return "sessionNoAuthenticated/successGet";
    }

    /**
     * セッション試験のWelcomeページ表示
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "sessionNoAuthenticated/index";
    }

}
