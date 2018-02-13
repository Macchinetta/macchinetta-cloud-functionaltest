/*
 * Copyright 2014-2017 NTT Corporation.
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

import static com.codeborne.selenide.Selenide.$;

public class TopPage {

    private String counter;
    private String csrf;
    private String transactionToken;

    public TopPage(String counter, String csrf, String transactionToken) {
        this.counter = counter;
        this.csrf = csrf;
        this.transactionToken = transactionToken;
    }

    public TopPage() {}

    public void logout() {
        $(By.id("logout")).submit();
    }

    public ConfirmTokenPage confirmToken() {
        $(By.id("command")).submit();
        return new ConfirmTokenPage($(By.id("result")).text());
    }

    public LoggingPage logging() {
        $(By.id("logging")).click();
        return new LoggingPage();
    }

    public ShowCustomViewPage showCustomView() {
        $(By.id("showCustomView")).click();
        return new ShowCustomViewPage($(By.id("viewName")).text());
    }

    public CustomErrorPage customError() {
        $(By.id("customError")).click();
        return new CustomErrorPage($(By.id("error-message")).text());
    }

    public String getCounter() {
        return counter;
    }

    public String getCsrf() {
        return csrf;
    }

    public String getTransactionToken() {
        return transactionToken;
    }
}
