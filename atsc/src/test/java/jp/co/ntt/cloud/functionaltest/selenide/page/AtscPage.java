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

import org.openqa.selenium.By;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class AtscPage {

    private SelenideElement newState;

    private SelenideElement newStateReason;

    private SelenideElement metricName;

    private SelenideElement namespace;

    private SelenideElement dimensions;

    /**
     * リスナーからアラーム通知イベントを取得する。
     * @return アラーム通知ページ情報
     */
    public AtscPage submit() {
        $(By.id("command")).submit();
        this.newState = $(By.id("newState"));
        this.newStateReason = $(By.id("newStateReason"));
        this.metricName = $(By.id("metricName"));
        this.namespace = $(By.id("namespace"));
        this.dimensions = $(By.id("dimensions"));
        return this;
    }

    public SelenideElement getNewState() {
        return newState;
    }

    public SelenideElement getNewStateReason() {
        return newStateReason;
    }

    public SelenideElement getMetricName() {
        return metricName;
    }

    public SelenideElement getDimensions() {
        return dimensions;
    }

    public SelenideElement getNamespace() {
        return namespace;
    }
}
