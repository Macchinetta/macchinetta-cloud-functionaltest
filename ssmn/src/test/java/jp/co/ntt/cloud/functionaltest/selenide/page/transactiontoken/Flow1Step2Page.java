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
 *
 */
package jp.co.ntt.cloud.functionaltest.selenide.page.transactiontoken;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

import com.codeborne.selenide.SelenideElement;

/**
 * TransactionToken作成後に表示されるページ
 */
public class Flow1Step2Page {

    private SelenideElement transactionToken;

    private SelenideElement result;

    /**
     * Flow1Step2Pageのコンストラクタ
     */
    public Flow1Step2Page() {
        this.transactionToken = $(byName("_TRANSACTION_TOKEN"));
        this.result = $(byId("result"));
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.IN)」が付与されたメソッドへのリクエスト
     * @return Flow1Step3Page
     */
    public Flow1Step3Page clickBtnIn() {
        $(byId("btn-in")).click();
        return page(Flow1Step3Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.END)」が付与されたメソッドへのリクエスト
     * @return Flow1Step4Page
     */
    public Flow1Step4Page clickBtnEnd() {
        $(byId("btn-end")).click();
        return page(Flow1Step4Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.END)」が付与されたメソッドへのリクエスト<br>
     * 擬似的なBusiness Errorが発生し、最初のステップに戻る
     * @return FlowAllStep1Page
     */
    public FlowAllStep1Page clickBtnEndError() {
        $(byId("btn-end-error")).click();
        return page(FlowAllStep1Page.class);
    }

    /**
     * 新規タブを開く
     * @return IndexPage
     */
    public IndexPage clickOpenNewWindow() {
        $(byId("open-new-window")).click();
        return page(IndexPage.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.CHECK)」が付与されたメソッドへのリクエスト<br>
     * 擬似的なファイルダウンロードを行う
     */
    public void clickBtnDownload01() {
        $(byId("btn-download01")).click();
    }

    /**
     * TransactionTokenCheckを行わず前画面に戻る
     * @return FlowAllStep1Page
     */
    public FlowAllStep1Page clickRedo1() {
        $(byName("redo1")).click();
        return page(FlowAllStep1Page.class);
    }

    /**
     * 「@TransactionTokenCheck("NameSpace")」が付与されたクラスの<br>
     * 「@TransactionTokenCheck(type = TransactionTokenType.CHECK)」が付与されたメソッドへのリクエスト
     * @return Flow1Step3Page
     */
    public Flow1Step3Page clickBtnCheck() {
        $(byId("btn-check")).click();
        return page(Flow1Step3Page.class);
    }

    /**
     * hidden項目のTransactionTokenの要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getTransactionToken() {
        return transactionToken;
    }

    /**
     * TransactionToken値表示用の要素を返却する
     * @return SelenideElement
     */
    public SelenideElement getResult() {
        return result;
    }

}
