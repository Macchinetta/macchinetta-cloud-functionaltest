package jp.co.ntt.cloud.functionaltest.app.common.constants;

public class WebPagePathConstants {

    /**
     * View論理名：ホーム画面
     */
    public static final String WELCOME_HOME = "welcome/home";

    /**
     * リクエストマッピング：cacheをheapから取得
     */
    public static final String HEAP = "heap";

    /**
     * リクエストマッピング：cacheをredisから取得
     */
    public static final String REDIS = "redis";

    /**
     * リクエストマッピング：heapのcasheを削除
     */
    public static final String HEAP_DELETECACHE = "heap/deleteCache";

    /**
     * リクエストマッピング：redisのcasheを削除
     */
    public static final String REDIS_DELETECACHE = "redis/deleteCache";

    /**
     * リダイレクト：cacheをheapから取得
     */
    public static final String REDIRECT_HEAP = "redirect:/heap";

    /**
     * リダイレクト：cacheをredisから取得
     */
    public static final String REDIRECT_REDIS = "redirect:/redis";

}
