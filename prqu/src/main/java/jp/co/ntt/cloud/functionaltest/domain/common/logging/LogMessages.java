/*
 * Copyright 2014-2017 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.domain.common.logging;

import java.text.MessageFormat;

/**
 * ログメッセージを定義する列挙型。
 * @author NTT 電電太郎
 */
public enum LogMessages {


    /**
     * 2重受信が発生した事を通知するログメッセージ。
     */
    W_FT_PQ_L9001("w_ft_pq_l9001", "二重受信が発生しました。メッセージID= {0}"),

    /**
     * リスナー処理開始ログメッセージ。
     */
    I_FT_PQ_L0001("i_ft_pq_l0001", "メッセージ受信処理開始 キュー名:{0}, メッセージID:{1}"),

    /**
     * リスナー処理終了ログメッセージ。
     */
    I_FT_PQ_L0002("i_ft_pq_l0002", "メッセージ受信処理終了 キュー名:{0}, メッセージID:{1}");

    /**
     * メッセージコード。
     */
    private final String code;

    /**
     * メッセージ内容。
     */
    private final String message;

    /**
     * コンストラクタ。
     * @param code メッセージコード
     * @param message メッセージ内容
     */
    private LogMessages(String code, String message) {
        this.code = code;
        this.message = "[" + code + "] " + message;
    }

    /**
     * メッセージコードを取得する。
     * @return メッセージコード
     */
    public String getCode() {
        return code;
    }

    /**
     * メッセージ内容を取得する。
     * @return メッセージ内容
     */
    public String getMessage() {
        return message;
    }

    /**
     * パラメータを指定してメッセージ内容を取得する。
     * @param args パラメータ
     * @return メッセージ内容
     */
    public String getMessage(Object... args) {
        return MessageFormat.format(message, args);
    }
}
