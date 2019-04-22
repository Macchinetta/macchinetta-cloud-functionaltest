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

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

/*
 * トップページのページオブジェクトクラス。
 */
//@formatter:off
public class TopPage {

    /**
     * 顧客再読み込み
     * @return TopPage トップページ
     */
    public TopPage reload() {
        $(byText("reload")).click();
        return this;
    }

    /**
     * 顧客登録
     * @param lastName 姓
     * @param firstName 名
     * @return TopPage トップページ
     */
    public TopPage register(String lastName, String firstName) {
        $(byId("inputLastName")).clear();
        $(byId("inputLastName")).setValue(lastName);
        $(byId("inputFirstName")).clear();
        $(byId("inputFirstName")).setValue(firstName);
        $("#button_register").click();
        return this;
    }
}
