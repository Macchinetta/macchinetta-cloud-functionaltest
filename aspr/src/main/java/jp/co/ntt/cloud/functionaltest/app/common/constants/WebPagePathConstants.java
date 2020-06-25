package jp.co.ntt.cloud.functionaltest.app.common.constants;

public class WebPagePathConstants {

    /**
     * リクエストマッピング：ホーム画面表示
     */
    public static final String ROOT_HOME = "/";

    /**
     * View論理名：ホーム画面
     */
    public static final String RESERVATION_HOME = "reservation/home";

    /**
     * リクエストマッピング：メッセージ送信 + メッセージ
     */
    public static final String SEND_MESSAGE = "send/{message}";

    /**
     * リクエストマッピング：メッセージ返信 + メッセージ件数
     */
    public static final String RECEIVE_COUNT = "receive/{count}";

    /**
     * リクエストマッピング：返信同期
     */
    public static final String RECEIVESYNC = "receiveSync";

    /**
     * リクエストマッピング：画面クリア
     */
    public static final String CLEAR = "clear";

    /**
     * リクエストマッピング：リスナ停止
     */
    public static final String STOPLISTENER = "stopListener";

    /**
     * リクエストマッピング：リスナ起動
     */
    public static final String STARTLISTENER = "startListener";

    /**
     * リクエストマッピング：メッセージ件数カウント
     */
    public static final String COUNT = "count";

    /**
     * リクエストマッピング：メッセージ表示 + 対象メッセージキュー
     */
    public static final String BROWSE_QUEUENAME = "browse/{queueName}";

    /**
     * リクエストマッピング：メッセージ全削除 + 対象メッセージキュー
     */
    public static final String DELETEALL_QUEUENAME = "deleteAll/{queueName}";

}
