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
import org.springframework.web.bind.annotation.PostMapping;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;

@Controller
@TransactionTokenCheck(namespace = "testTokenAttrByNameSpace")
public class TransactionTokenFlow7Controller {

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACE, params = "confirm")
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    public String flow1NamespaceStep2() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACESTEP2;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACE, params = "intermediate")
    @TransactionTokenCheck(type = TransactionTokenType.IN)
    public String flow1NamespaceStep3() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACESTEP3;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACE, params = "finalize")
    @TransactionTokenCheck(type = TransactionTokenType.END)
    public String flow1NamespaceStep4() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACESTEP4;
    }

    @PostMapping(value = WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACE, params = "check")
    @TransactionTokenCheck(type = TransactionTokenType.CHECK)
    public String flow1Step2Check() {
        return WebPagePathConstants.TRANSACTIONTOKEN_FLOW1NAMESPACESTEP3;
    }
}
