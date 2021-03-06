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
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;

/**
 * Loggingページのページオブジェクトクラス。
 */
public class LoggingPage {

    /**
     * UUIDを送信する。
     * @param uuid
     * @return LoggingPage
     */
    public LoggingPage send(String uuid) {
        $(byName("uuid")).setValue(uuid);
        $(byId("send")).click();
        return this;
    }

    /**
     * ログアウトする。
     */
    public void logout() {
        $(byId("logout")).click();
    }
}
