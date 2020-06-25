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
package jp.co.ntt.cloud.functionaltest.app.transactiontoken;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

@Controller
public class TransactionTokenFunctionalTestController {

    @GetMapping(value = WebPagePathConstants.TRANSACTIONTOKEN)
    public String index() {
        return WebPagePathConstants.TRANSACTIONTOKEN_INDEX;
    }

    // Contents confirmation testing
    @GetMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_CREATE)
    public String functionTestContentsConfirmation() {
        return WebPagePathConstants.TRANSACTIONTOKEN_CREATEINPUT;
    }

    // flow testing
    @GetMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW)
    public String functionTestFlow() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOWALLSTEP1;
    }

    // flow @AliasFor namespace testing
    @GetMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOWNAMESPACE)
    public String functionTestFlowNamespace() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOWALLNAMESPACESTEP1;
    }

}
