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

import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement newState;

    private SelenideElement newStateReason;

    private SelenideElement metricName;

    private SelenideElement namespace;

    private SelenideElement dimensions;

    /**
     * HomePageのコンストラクタ。
     */
    public HomePage() {
        this.newState = $(byId("newState"));
        this.newStateReason = $(byId("newStateReason"));
        this.metricName = $(byId("metricName"));
        this.namespace = $(byId("namespace"));
        this.dimensions = $(byId("dimensions"));
    }

    /**
     * リスナーからアラーム通知イベントを取得する。
     * @return HomePage
     */
    public HomePage clickListen() {
        $(byId("listen")).click();
        return this;
    }

    /**
     * 状態変更を取得する。
     * @return SelenideElement
     */
    public SelenideElement getNewState() {
        return newState;
    }

    /**
     * 理由を取得する。
     * @return SelenideElement
     */
    public SelenideElement getNewStateReason() {
        return newStateReason;
    }

    /**
     * メトリクス名を取得する。
     * @return SelenideElement
     */
    public SelenideElement getMetricName() {
        return metricName;
    }

    /**
     * 名前空間を取得する。
     * @return SelenideElement
     */
    public SelenideElement getDimensions() {
        return dimensions;
    }

    /**
     * ディメンジョンを取得する。
     * @return SelenideElement
     */
    public SelenideElement getNamespace() {
        return namespace;
    }
}
