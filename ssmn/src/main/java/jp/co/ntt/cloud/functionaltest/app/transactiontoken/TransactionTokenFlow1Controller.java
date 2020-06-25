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
import org.springframework.web.bind.annotation.PostMapping;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenContext;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

@Controller
@TransactionTokenCheck("transactiontoken")
public class TransactionTokenFlow1Controller {

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "confirm")
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    public String flow1Step2() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP2;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "confirmError")
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    public String flow1Step2_withError() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOWALLSTEP1;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "confirmDiffNamespace")
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    public String flow1Step2_toDifferentNamespace() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP2TODIFFERENTNAMESPACE;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "redo1")
    public String flow1Step2Back() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOWALLSTEP1;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "intermediate")
    @TransactionTokenCheck(type = TransactionTokenType.IN)
    public String flow1Step3() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP3;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "intermediateWithFinish")
    @TransactionTokenCheck(type = TransactionTokenType.IN)
    public String flow1Step3_withFinish(
            TransactionTokenContext transactionTokenContext) {
        // Navigate to final screen
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP4;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "intermediateWithFinishError")
    @TransactionTokenCheck(type = TransactionTokenType.IN)
    public String flow1Step3_withFinishError(
            TransactionTokenContext transactionTokenContext) {
        // Navigate to intermediate screen again
        // Transaction token will be updated
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP2;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "redo2")
    @TransactionTokenCheck
    public String flow1Step3Back() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP2;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "finalize")
    @TransactionTokenCheck(type = TransactionTokenType.END)
    public String flow1Step4() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP4;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "finalizeError")
    @TransactionTokenCheck(type = TransactionTokenType.END)
    public String flow1Step4_withError() {
        // return to first step screen due to business error
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOWALLSTEP1;
    }

    @GetMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "download01")
    @TransactionTokenCheck(type = TransactionTokenType.CHECK)
    public String flow1Step2Download_01() {
        return WebPagePathConstants.IMAGEFILEDOWNLOADVIEW;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1, params = "check")
    @TransactionTokenCheck(type = TransactionTokenType.CHECK)
    public String flow1Step2Check() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1STEP3;
    }

}
