package jp.co.ntt.cloud.functionaltest.app.common.constants;

public class WebPagePathConstants {

    /**
     * リクエストマッピング：ホーム画面遷移
     */
    public static final String ROOT_HOME = "/";

    /**
     * View論理名：ホーム画面
     */
    public static final String WELCOME_HOME = "welcome/home";

    /**
     * リクエストマッピング・View論理名：トランザクショントークンチェック
     */
    public static final String TRANSACTION_CONFIRMTOKEN = "transactiontoken/confirmToken";

    /**
     * リクエストマッピング・View論理名：トランザクショントークン生成
     */
    public static final String TRANSACTION_GENERATETOKEN = "transactiontoken/generateToken";

    /**
     * リクエストマッピング：カスタムビュー遷移画面
     */
    public static final String SHOWCUSTOMVIEW = "showCustomView";

    /**
     * View論理名：カスタムビュー画面（spring-mvc.xml中のviewResolverによる解決）
     */
    public static final String CWAPCUSTOMVIEW = "cwapCustomView";

    /**
     * リクエストマッピング・View論理名：ログ生成
     */
    public static final String LOGGER_LOGGING = "logger/logging";

    /**
     * リクエストマッピング・View論理名：ログ出力
     */
    public static final String LOGGER_LOGGED = "logger/logged";

    /**
     * リクエストマッピング：カスタム例外画面遷移
     */
    public static final String CUSTOMERROR = "customError";

}
