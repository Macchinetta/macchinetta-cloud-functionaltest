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
package jp.co.ntt.cloud.functionaltest.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * ファンクショナルテストメッセージを表すクラス。
 * @author NTT 電電太郎
 */
public class FTMessage implements Serializable {

    /**
     * シリアルバージョンUID
     */
    private static final long serialVersionUID = 6866250365556483864L;

    /**
     * 依頼ID
     */
    private String requestId;

    /**
     * メッセージ
     */
    private String message;

    /**
     * 依頼日時
     */
    private Date requestedAt;

    /**
     * 処理日時
     */
    private Date processedAt;

    /**
     * コンストラクタ。
     */
    public FTMessage() {

    }

    /**
     * コンストラクタ
     * @param message メッセージ
     */
    public FTMessage(String message, String requestId) {
        this.message = message;
        this.requestId = requestId;
        this.requestedAt = new Date();
    }



    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "FTMessage [requestId=" + requestId + ", message=" + message
                + ", requestedAt=" + requestedAt + ", processedAt="
                + processedAt + "]";
    }

}
