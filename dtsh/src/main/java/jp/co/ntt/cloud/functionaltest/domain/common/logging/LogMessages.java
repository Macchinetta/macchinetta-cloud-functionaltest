/*
 * Copyright(c) 2017 NTT Corporation.
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
 */
package jp.co.ntt.cloud.functionaltest.domain.common.logging;

import java.text.MessageFormat;

/**
 * ログメッセージを定義する列挙型。
 * @author NTT 電電太郎
 */
public enum LogMessages {

    /**
     * 不正リクエスト(パラメータ改竄)が発生した事を通知するログメッセージ。
     */
    E_AR_A0_L9001("e.ar.a0.L9001", "不正リクエスト(パラメータ改竄) {0}"),

    /**
     * データの更新(登録/削除)件数が想定件数が一致しない事を通知するログメッセージ。
     */
    E_AR_A0_L9002("e.ar.a0.L9002", "データ更新件数({0})と予想件数({1})の不一致"),

    /**
     * S3へのアクセスでエラーが発生した事を通知するログメッセージ。
     */
    E_AR_A0_L9003("e.ar.a0.L9003", "S3へのアクセスで例外が発生しました {0}"),

    /**
     * ルーティング用データソースの設定がない事を通知するログメッセージ。
     */
    E_AR_A0_L9005("e.ar.a0.L9005", "ルーティング用データソースの設定がされていません。"),

    /**
     * ルーティング用デフォルトデータソースの設定がない事を通知するログメッセージ。
     */
    E_AR_A0_L9006("e.ar.a0.L9006", "ルーティング用デフォルトデータソースの設定がされていません。"),

    /**
     * ルーティング用データソースの設定が重複している事を通知するログメッセージ。
     */
    E_AR_A0_L9007("e.ar.a0.L9007", "ルーティング用データソースの設定が重複しています。キー= {0}"),

    /**
     * ルーティング用データソースの作成中に例外が発生した事を通知するログメッセージ。
     */
    E_AR_A0_L9008("e.ar.a0.L9008", "ルーティング用データソースの作成中に例外が発生しました。"),

    /**
     * 指定されたデータベースのインスタンスが存在しない事を通知するログメッセージ。
     */
    E_AR_A0_L9009("e.ar.a0.L9009",
            "指定されたデータベースのインスタンスが存在しません。Database ID= {0}"),

    /**
     * 指定されたプロパティキーが不正である事を通知するログメッセージ。
     */
    E_AR_A0_L9010("e.ar.a0.L9010",
            "指定されたプロパティキー[{0}]を指定する場合は、データソースファクトリを拡張して下さい。"),

    /**
     * 2重受信が発生した事を通知するログメッセージ。
     */
    W_AR_A0_L9001("w.ar.a0.L9001", "二重受信が発生しました。メッセージID= {0}"),

    /**
     * ログインが成功した事を通知するログメッセージ。
     */
    I_AR_A1_L0001("i.ar.a1.L0001", "お客様番号[{0}] ログイン成功"),

    /**
     * お客様番号に指定された値が不正である事が原因で、ログインが失敗した事を通知するログメッセージ。
     */
    I_AR_A1_L2001("i.ar.a1.L2001", "お客様番号[{0}] ログイン失敗(お客様番号不正)"),

    /**
     * パスワードに指定された値が不正である事が原因で、ログインが失敗した事を通知するログメッセージ。
     */
    I_AR_A1_L2002("i.ar.a1.L2002", "お客様番号[{0}] ログイン失敗(パスワード不正)"),

    /**
     * パスワードが一致しない事を通知するログメッセージ。
     */
    I_AR_A1_L2003("i.ar.a1.L2003", "お客様番号[{0}] ログイン失敗(パスワード不一致)"),

    /**
     * ログアウトが成功した事を通知するログメッセージ。
     */
    I_AR_A2_L0001("i.ar.a2.L0001", "お客様番号[{0}] ログアウト成功");

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
