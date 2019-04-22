/*
 * Copyright(c) 2017 NTT Corporation.
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
import static com.codeborne.selenide.Selenide.page;

public class MlsnPage {

    public MlsnPage send(String toAddress, String type, String body) {
        $(By.name("to")).setValue(toAddress);
        $(By.name("kind")).setValue(type);
        $(By.name("body")).setValue(body);
        $(By.id("sendMail")).submit();
        return page(MlsnPage.class);
    }

    public String messageId() {
        return $(By.id("messageId")).text();
    }

    public String topicArn() {
        return $(By.id("topicArn")).text();
    }

    public String notificationType() {
        return $(By.id("notificationType")).text();
    }

    public String headers() {
        return $(By.id("headers")).text();
    }
}
