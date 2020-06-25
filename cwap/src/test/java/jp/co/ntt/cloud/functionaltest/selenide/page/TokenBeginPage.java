package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class TokenBeginPage {

    private SelenideElement transactionToken;

    /**
     * TokenGenerateページのコンストラクタ。
     */
    public TokenBeginPage() {
        this.transactionToken = $(byName("_TRANSACTION_TOKEN"));
    }

    /**
     * transactionTokenCheckをクリックする。
     * @return TokenCheckPage
     */
    public TokenCheckPage confirmToken() {
        $(byId("transactionTokenCheck")).click();
        return new TokenCheckPage();
    }

    /**
     * トランザクショントークンの要素を返却する。
     * @return SelenideElement
     */
    public SelenideElement getTransactionToken() {
        return transactionToken;
    }

}
