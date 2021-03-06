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
package jp.co.ntt.cloud.functionaltest.app.transactiontoken.customstoresize;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

@Controller
@TransactionTokenCheck("transactiontoken/customTransactionStoreSize2")
public class TransactionTokenCustomFlow2Controller {

    @GetMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_INDEX)
    public String customflowMenu() {
        return WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMSTORESIZEMENU;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_CREATEFLOW_1, params = "confirm")
    @TransactionTokenCheck(value = "create", type = TransactionTokenType.BEGIN)
    public String customflowStepBegin() {
        return WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMSTORESIZENEXT;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_CREATEFLOW_1, params = "confirmOther")
    @TransactionTokenCheck(value = "createOther", type = TransactionTokenType.BEGIN)
    public String customflowStepBeginOther() {
        return WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMSTORESIZENEXT;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_CREATEFLOW_1, params = "intermediate")
    @TransactionTokenCheck(value = "create", type = TransactionTokenType.IN)
    public String customflowStepIn() {
        return WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMSTORESIZENEXT;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_CREATEFLOW_1, params = "intermediateOther")
    @TransactionTokenCheck(value = "createOther", type = TransactionTokenType.IN)
    public String customflowStepInOther() {
        return WebPagePathConstants.TRANSACTIONTOKEN_CUSTOMSTORESIZENEXT;
    }
}
