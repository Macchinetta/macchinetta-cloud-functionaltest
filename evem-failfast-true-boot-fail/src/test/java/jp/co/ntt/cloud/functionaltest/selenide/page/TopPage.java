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
    public HelloPage login(String userId, String password){
        $(byId("userId")).clear();
        $(byId("userId")).setValue(userId);
        $(byId("password")).clear();
        $(byId("password")).setValue(password);
        $$("tr").get(2).$("input").click();
        return new HelloPage();
    }
}
