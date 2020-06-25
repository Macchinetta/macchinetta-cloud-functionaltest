package jp.co.ntt.cloud.functionaltest.app.common.constants;

public class WebPagePathConstants {

    /**
     * リクエストマッピング：商品確認画面へ遷移
     */
    public static final String CART = "cart";

    /**
     * リクエストマッピング：商品追加
     */
    public static final String CART_ADD = "cart/add";

    /**
     * リクエストマッピング：商品変更
     */
    public static final String CART_CHANGE = "cart/change";

    /**
     * リクエストマッピング：商品削除
     */
    public static final String CART_DELETE = "cart/delete";

    /**
     * View論理名：商品確認画面
     */
    public static final String CART_VIEWCART = "cart/viewCart";

    /**
     * View論理名：セッション無効
     */
    public static final String ERROR_INVALIDSESSION = "error/invalidSession";

    /**
     * View論理名：セッション無効エラー画面
     */
    public static final String COMMON_ERROR_SESSIONINVALIDERROR = "common/error/sessionInvalidError";

    /**
     * リクエストマッピング：注文画面表示
     */
    public static final String ORDER_FORM = "order/form";

    /**
     * View論理名：注文画面
     */
    public static final String ORDER_ORDERFORM = "order/orderForm";

    /**
     * リクエストマッピング：確認画面表示
     */
    public static final String ORDER_CONFIRM = "order/confirm";

    /**
     * View論理名：確認画面
     */
    public static final String ORDER_ORDERCONFIRM = "order/orderConfirm";

    /**
     * リクエストマッピング：完了画面表示
     */
    public static final String ORDER_FINISH = "order/finish";

    /**
     * View論理名：完了画面
     */
    public static final String ORDER_ORDERFINISH = "order/orderFinish";

    /**
     * リクエストマッピング：認証ありセッション試験実行
     */
    public static final String SESSION_ISAUTHENTICATED = "session/isAuthenticated";

    /**
     * View論理名：認証ありセッション試験POST送信画面
     */
    public static final String SESSIONISAUTHENTICATED_SUCCESSPOST = "sessionIsAuthenticated/successPost";

    /**
     * View論理名：認証ありセッション試験GET送信画面
     */
    public static final String SESSIONISAUTHENTICATED_SUCCESSGET = "sessionIsAuthenticated/successGet";

    /**
     * View論理名：認証ありセッション試験一覧画面
     */
    public static final String SESSIONISAUTHENTICATED_INDEX = "sessionIsAuthenticated/index";

    /**
     * リクエストマッピング：認証なしセッション試験実行
     */
    public static final String SESSION_NOAUTHENTICATED = "session/noAuthenticated";

    /**
     * View論理名：認証なしセッション試験POST送信画面
     */
    public static final String SESSIONNOAUTHENTICATED_SUCCESSPOST = "sessionNoAuthenticated/successPost";

    /**
     * View論理名：認証なしセッション試験GET送信画面
     */
    public static final String SESSIONNOAUTHENTICATED_SUCCESSGET = "sessionNoAuthenticated/successGet";

    /**
     * View論理名：認証なしセッション試験画面
     */
    public static final String SESSIONNOAUTHENTICATED_INDEX = "sessionNoAuthenticated/index";

    /**
     * リクエストマッピング：カスタムトランザクショントークン検証用一覧画面遷移
     */
    public static final String TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE1_INDEX = "transactiontoken/customTransactionStoreSize1/index";

    /**
     * View論理名：カスタムトランザクショントークン検証用メニュー画面
     */
    public static final String TRANSACTIONTOKEN_CUSTOMSTORESIZEMENU = "transactiontoken/customStoreSizeMenu";

    /**
     * リクエストマッピング：カスタムトランザクショントークン検証用作成画面遷移
     */
    public static final String TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE1_CREATEFLOW = "transactiontoken/customTransactionStoreSize1/createFlow";

    /**
     * View論理名：カスタムトランザクショントークン検証用継続画面
     */
    public static final String TRANSACTIONTOKEN_CUSTOMSTORESIZENEXT = "transactiontoken/customStoreSizeNext";

    /**
     * リクエストマッピング：カスタムトランザクショントークン検証用一覧画面遷移
     */
    public static final String TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_INDEX = "transactiontoken/customTransactionStoreSize2/index";

    /**
     * リクエストマッピング：カスタムトランザクショントークン検証用作成画面遷移
     */
    public static final String TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_CREATEFLOW_1 = "transactiontoken/customTransactionStoreSize2/createFlow_1";

    /**
     * リクエストマッピング：カスタムトランザクショントークン検証用作成画面遷移
     */
    public static final String TRANSACTIONTOKEN_CUSTOMTRANSACTIONSTORESIZE2_CREATEFLOW_2 = "transactiontoken/customTransactionStoreSize2/createFlow_2";

    /**
     * リクエストマッピング：トランザクショントークン作成1-1
     */
    public static final String TRANSACTIONTOKEN_CREATE_1_1 = "transactiontoken/create/1_1";

    /**
     * リクエストマッピング：トランザクショントークン作成1-2
     */
    public static final String TRANSACTIONTOKEN_CREATE_1_2 = "transactiontoken/create/1_2";

    /**
     * リクエストマッピング：トランザクショントークン作成1-3
     */
    public static final String TRANSACTIONTOKEN_CREATE_1_3 = "transactiontoken/create/1_3";

    /**
     * リクエストマッピング：トランザクショントークン作成1-4
     */
    public static final String TRANSACTIONTOKEN_CREATE_1_4 = "transactiontoken/create/1_4";

    /**
     * View論理名：トランザクショントークン出力画面
     */
    public static final String TRANSACTIONTOKEN_CREATEOUTPUT = "transactiontoken/createOutput";

    /**
     * リクエストマッピング：トランザクショントークン作成(アンダースコアつき)1-1
     */
    public static final String TRANSACTIONTOKEN_CREATEWITHUNDERSCORE_1_1 = "transactiontoken/createWithUnderscore/1_1";

    /**
     * リクエストマッピング：トランザクショントークン作成(アンダースコアつき)1-2
     */
    public static final String TRANSACTIONTOKEN_CREATEWITHUNDERSCORE_1_2 = "transactiontoken/createWithUnderscore/1_2";

    /**
     * View論理名：トランザクショントークン出力(アンダースコアつき)画面
     */
    public static final String TRANSACTIONTOKEN_CREATEOUTPUTUNDERSCORE = "transactiontoken/createOutputUnderscore";

    /**
     * リクエストマッピング：トランザクショントークン試験画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1 = "transactiontoken/flow1";

    /**
     * View論理名：トランザクショントークン試験ステップ2画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1STEP2 = "transactiontoken/flow1Step2";

    /**
     * View論理名：トランザクショントークン試験全ステップ画面
     */
    public static final String TRANSACTIONTOKEN_FLOWALLSTEP1 = "transactiontoken/flowAllStep1";

    /**
     * View論理名：トランザクショントークン試験ステップ2(異なるネームスペース間)画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1STEP2TODIFFERENTNAMESPACE = "transactiontoken/flow1Step2ToDifferentNamespace";

    /**
     * View論理名：トランザクショントークン試験ステップ3画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1STEP3 = "transactiontoken/flow1Step3";

    /**
     * View論理名：トランザクショントークン試験ステップ4画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1STEP4 = "transactiontoken/flow1Step4";

    /**
     * View論理名：リダイレクト カート画面
     */
    public static final String REDIRECT_CART = "redirect:/cart";

    /**
     * View論理名：ホーム画面
     */
    public static final String REDIRECT_ORDER_FORM = "redirect:/order/form";

    /**
     * View論理名：ホーム画面
     */
    public static final String IMAGEFILEDOWNLOADVIEW = "imageFileDownloadView";

    /**
     * リクエストマッピング：トランザクショントークン試験画面遷移
     */
    public static final String TRANSACTIONTOKEN_FLOW2 = "transactiontoken/flow2";

    /**
     * View論理名：トランザクショントークン試験ステップ2画面
     */
    public static final String TRANSACTIONTOKEN_FLOW2STEP2 = "transactiontoken/flow2Step2";

    /**
     * View論理名：トランザクショントークン試験ステップ3画面
     */
    public static final String TRANSACTIONTOKEN_FLOW2STEP3 = "transactiontoken/flow2Step3";

    /**
     * View論理名：トランザクショントークン試験ステップ4画面
     */
    public static final String TRANSACTIONTOKEN_FLOW2STEP4 = "transactiontoken/flow2Step4";

    /**
     * リクエストマッピング：トランザクショントークン試験画面遷移
     */
    public static final String TRANSACTIONTOKEN_FLOW6 = "transactiontoken/flow6";

    /**
     * View論理名：トランザクショントークン表示画面
     */
    public static final String TRANSACTIONTOKEN_TRANSACTIONTOKENDISPLAYSTART = "transactiontoken/transactionTokenDisplayStart";

    /**
     * View論理名：トランザクショントークン表示完了画面
     */
    public static final String TRANSACTIONTOKEN_TRANSACTIONTOKENDISPLAYFINISH = "transactiontoken/transactionTokenDisplayFinish";

    /**
     * リクエストマッピング：トランザクショントークン試験(ネームスペースつき)画面遷移
     */
    public static final String TRANSACTIONTOKEN_FLOW1NAMESPACE = "transactiontoken/flow1_namespace";

    /**
     * View論理名：トランザクショントークン試験(ネームスペースつき)ステップ2画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1NAMESPACESTEP2 = "transactiontoken/flow1NamespaceStep2";

    /**
     * View論理名：トランザクショントークン試験(ネームスペースつき)ステップ3画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1NAMESPACESTEP3 = "transactiontoken/flow1NamespaceStep3";

    /**
     * View論理名：トランザクショントークン試験(ネームスペースつき)ステップ4画面
     */
    public static final String TRANSACTIONTOKEN_FLOW1NAMESPACESTEP4 = "transactiontoken/flow1NamespaceStep4";

    /**
     * リクエストマッピング：トランザクショントークン画面に遷移
     */
    public static final String TRANSACTIONTOKEN = "transactiontoken";

    /**
     * View論理名：トランザクショントークン画面
     */
    public static final String TRANSACTIONTOKEN_INDEX = "transactiontoken/index";

    /**
     * リクエストマッピング：トランザクショントークン作成
     */
    public static final String TRANSACTIONTOKEN_CREATE = "transactiontoken/create";

    /**
     * リクエストマッピング：トランザクショントークン試験画面
     */
    public static final String TRANSACTIONTOKEN_FLOW = "transactiontoken/flow";

    /**
     * リクエストマッピング：トランザクショントークン試験(ネームスペースつき)画面遷移
     */
    public static final String TRANSACTIONTOKEN_FLOWNAMESPACE = "transactiontoken/flow_namespace";

    /**
     * View論理名：トランザクショントークン作成画面
     */
    public static final String TRANSACTIONTOKEN_CREATEINPUT = "transactiontoken/createInput";

    /**
     * View論理名：トランザクショントークン試験(ネームスペースつき)画面
     */
    public static final String TRANSACTIONTOKEN_FLOWALLNAMESPACESTEP1 = "transactiontoken/flowAllNamespaceStep1";

    /**
     * リクエストマッピング：ホーム画面遷移
     */
    public static final String ROOT_HOME = "/";

    /**
     * View論理名：ホーム画面
     */
    public static final String WELCOME_HOME = "welcome/home";

}
