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
package jp.co.ntt.cloud.functionaltest.selenide.page;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class AtscPage {

    private String newState;
    private String newStateReason;
    private String metricName;
    private String namespace;
    private String dimensions;

    /**
     * リスナーからアラーム通知イベントを取得する。
     *
     * @return アラーム通知ページ情報
     */
    public AtscPage submit() {
        $(By.id("command")).submit();
        this.newState = $(By.id("newState")).text();
        this.newStateReason = $(By.id("newStateReason")).text();
        this.metricName = $(By.id("metricName")).text();
        this.namespace = $(By.id("namespace")).text();
        this.dimensions = $(By.id("dimensions")).text();
        return this;
    }

    public String getNewState() {
        return newState;
    }

    public String getNewStateReason() {
        return newStateReason;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getNamespace() {
        return namespace;
    }
}
