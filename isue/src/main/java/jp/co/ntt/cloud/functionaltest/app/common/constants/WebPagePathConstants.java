package jp.co.ntt.cloud.functionaltest.app.common.constants;

public class WebPagePathConstants {

    /**
     * リクエストマッピング：ホーム画面遷移（検索画面へ）
     */
    public static final String ROOT_HOME = "/";

    /**
     *リクエストマッピング：メンテナンス画面遷移
     */
    public static final String MAINTENANCE = "maintenance";

    /**
     *リクエストマッピング：アップロード
     */
    public static final String UPLOAD = "upload";

    /**
     * リクエストマッピング：削除
     */
    public static final String DELETE = "delete";

    /**
     * リクエストマッピング：検索
     */
    public static final String SEARCH = "search";

    /**
     * View論理名：メンテナンス画面
     */
    public static final String FILEUPLOAD_MAINTENANCE = "fileupload/maintenance";

    /**
     * View論理名：検索画面（ホーム画面）
     */
    public static final String FILEUPLOAD_SEARCH = "fileupload/search";

    /**
     * リダイレクト：アップロード（完了クエリつき）
     */
    public static final String REDIRECT_UPLOAD_COMPLETE = "redirect:/upload?complete";

    /**
     * リダイレクト：削除（完了クエリつき）
     */
    public static final String REDIRECT_DELETE_COMPLETE = "redirect:/delete?complete";

}
