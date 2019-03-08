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
import static com.codeborne.selenide.Selenide.$$;

/*
 * トップページのページオブジェクトクラス。
 */
public class TopPage {

    /**
     * 顧客番号とパスワードを使用してログインする。
     * @param userId 顧客番号
     * @param password パスワード
     * @return TopPage トップページ
     */
    public SearchPage login(String userId, String password) {
        $(byId("userId")).clear();
        $(byId("userId")).setValue(userId);
        $(byId("password")).clear();
        $(byId("password")).setValue(password);
        $$("tr").get(2).$("input").click();
        return new SearchPage();
    }
}
