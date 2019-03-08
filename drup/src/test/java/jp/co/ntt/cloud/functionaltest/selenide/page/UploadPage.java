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
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;

/*
 * アップロードページのページオブジェクトクラス。
 */
public class UploadPage {

    /**
     * ログアウトする。
     * @return TopPage トップページ
     */
    public TopPage logout() {
        $(byId("logout")).click();
        return new TopPage();
    }

    /**
     * ファイルをアップロードする。
     */
    public void upload(File file) {
        $(byId("file")).uploadFile(file);
        $(byId("uploadFile")).click();
    }

    /**
     * ファイルをアップロードする。
     */
    public void uploadWithDelay(File file) {
        $(byId("file")).uploadFile(file);
        $(byId("cbox1")).setSelected(true);
        $(byId("uploadFile")).click();
    }

    /**
     * ログイン状態を返却する。
     * @return boolean ログイン状態
     */
    public boolean isLoggedIn() {
        if ($(byId("logout")).$(byText("Logout")).exists()) {
            return true;
        }
        return false;
    }
}
