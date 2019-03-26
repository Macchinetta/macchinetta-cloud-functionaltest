/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Loginページのページオブジェクトクラス。
 */
public class LoginPage {

    /**
     * 顧客番号とパスワードを使用してログインする。
     * @param userId 顧客番号
     * @param password パスワード
     * @return HomePage
     */
    public HomePage login(String userId, String password) {
        $(byId("userId")).setValue(userId);
        $(byId("password")).setValue(password);
        $$("tr").get(2).$("input").click();
        return new HomePage();
    }
}
