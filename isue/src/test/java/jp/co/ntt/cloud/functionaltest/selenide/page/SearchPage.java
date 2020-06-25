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
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.HashMap;
import java.util.Map;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

/**
 * Searchページのページオブジェクトクラス。
 */
public class SearchPage {

    private SelenideElement h;

    /**
     * SearchPageのコンストラクタ。
     */
    public SearchPage() {
        this.h = $("h1");
    }

    /**
     * ログアウトする。
     * @return LoginPage
     */
    public LoginPage logout() {
        $("button").click();
        return new LoginPage();
    }

    /**
     * ログイン状態を返却する。
     * @return boolean ログイン状態
     */
    public boolean isLoggedIn() {
        if ($(byId("wrapper")).$(byText("Logout")).exists()) {
            return true;
        }
        return false;
    }

    /**
     * オブジェクトキーを指定して検索する。
     * @param objectKey
     * @return SearchPage
     */
    public SearchPage searchByPk(String objectKey) {
        $(byId("objectKey")).setValue(objectKey);
        $$("tr").get(3).$("button").click();
        return new SearchPage();
    }

    /**
     * アップロードユーザとアップロード日を指定して検索する。
     * @param uploadUser
     * @param uploadDate
     * @return SearchPage
     */
    public SearchPage searchByIndex_uploadUser_uploadDate(String uploadUser,
            String uploadDate) {
        $(byId("uploadUser")).setValue(uploadUser);
        $(byId("uploadDate")).setValue(uploadDate);
        $$("tr").get(3).$("button").click();
        return new SearchPage();
    }

    /**
     * バケット名を指定して検索する。
     * @param bucketName
     * @return SearchPage
     */
    public SearchPage searchByIndex_bucketName(String bucketName) {
        $(byId("bucketName")).selectOption(bucketName);
        $$("tr").get(3).$("button").click();
        return new SearchPage();
    }

    /**
     * 検索結果の要素を返却する。
     * @return ElementsCollection
     */
    public ElementsCollection getRows() {
        return $(byId("result")).$$("tr");
    }

    /**
     * 検索結果TABLE.TRタグ内のTDデータをMapに変換する。
     * @param elements TABLE.TRタグのコンテンツ（1レコード分のデータ）
     * @return map 変換後Map
     */
    public Map<String, SelenideElement> toRecordMap(Integer rowNumber) {
        ElementsCollection elements = $(byId("result")).$$("tr").get(rowNumber)
                .$$("td");
        Map<String, SelenideElement> map = new HashMap<>();
        map.put("objectKey", elements.get(0));
        map.put("bucketName", elements.get(1));
        map.put("fileName", elements.get(2));
        map.put("size", elements.get(3));
        map.put("uploadUser", elements.get(4));
        map.put("uploadDate", elements.get(5));
        map.put("sequencer", elements.get(6));
        map.put("version", elements.get(7));
        return map;
    }

    /**
     * 見出しの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getH() {
        return h;
    }
}
