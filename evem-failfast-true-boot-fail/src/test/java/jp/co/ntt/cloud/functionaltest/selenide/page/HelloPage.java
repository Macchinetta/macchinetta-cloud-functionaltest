/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/*
 * Helloページのページオブジェクトクラス。
 */
public class HelloPage {

    /**
     * ログアウトする。
     * @return TopPage トップページ
     */
    public TopPage logout() {
        $("button").click();
        return new TopPage();
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
     * S3設定値を表示しているテーブルを返却する。
     * @return S3設定値テーブル
     */
    public SelenideElement getS3ConfigConfigurationPropertiesTable() {
        return $(byId("s3ConfigConfigurationPropertiesTable"));
    }

    /**
     * S3設定値を表示しているテーブルを返却する。
     * @return S3設定値テーブル
     */
    public SelenideElement getS3ConfigValueTable() {
        return $(byId("s3ConfigValueTable"));
    }
}
