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
package jp.co.ntt.cloud.functionaltest.app.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

/**
 * セッションが無効なときにエラーページを返却するコントローラ
 * @author NTT 電電太郎
 */
@Controller
public class SessionInvalidController {

    @GetMapping(value = WebPagePathConstants.ERROR_INVALIDSESSION)
    public String sessionInvalidErrorView() {
        return WebPagePathConstants.COMMON_ERROR_SESSIONINVALIDERROR;
    }

}
