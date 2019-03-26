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
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

import com.codeborne.selenide.SelenideElement;

/**
 * Indexページのページオブジェクトクラス。
 */
public class IndexPage {

    private SelenideElement selectedKey;

    private SelenideElement status;

    private SelenideElement s3Base64;

    private SelenideElement localBase64;

    /**
     * IndexPageページのコンストラクタ。
     */
    public IndexPage() {
        this.selectedKey = $(byId("selectedKey"));
        this.status = $(byId("status"));
        this.s3Base64 = $(byId("s3Base64"));
        this.localBase64 = $(byId("localBase64"));
    }

    /**
     * オブジェクトキーを指定してダウンロードする。
     * @param key
     * @return IndexPage
     */
    public IndexPage download(String key) {
        $(byId(key)).click();
        return page(IndexPage.class);
    }

    /**
     * オブジェクトキーの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getSelectedKey() {
        return selectedKey;
    }

    /**
     * ダウンロード結果の要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getStatus() {
        return status;
    }

    /**
     * BASE64データの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getS3Base64() {
        return s3Base64;
    }

    /**
     * APサーバ上のローカルファイルの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getLocalBase64() {
        return localBase64;
    }
}
