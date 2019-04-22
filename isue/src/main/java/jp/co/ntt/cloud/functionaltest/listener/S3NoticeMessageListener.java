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
package jp.co.ntt.cloud.functionaltest.listener;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

import jp.co.ntt.cloud.functionaltest.app.fileupload.SearchController;
import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;

/**
 * S3更新イベントメッセージリスナー
 * @author NTT 電電太郎
 */
@Component
public class S3NoticeMessageListener {

    /** ロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(
            SearchController.class);

    /** DBMapper */
    @Inject
    private DynamoDBMapper dbMapper;

    /** 追加更新時イベント名 */
    private static final String EV_CREATED = "ObjectCreated";

    /** 削除時イベント名 */
    private static final String EV_REMOVED = "ObjectRemoved";

    /**
     * S3更新イベントメッセージ受信処理。
     * @param recvMsg S3更新イベントメッセージ
     */
    @JmsListener(destination = "S3_UPDATE_NOTICE", concurrency = "1") // (1)
    public void receive(SQSTextMessage recvMsg) {
        logger.debug("▼▽▼▽▼ メッセージ受信処理開始 ▼▽▼▽▼");
        try {
            logger.debug(recvMsg.getText());

            // JSON -> Java
            S3EventNotification event = S3EventNotification.parseJson(recvMsg
                    .getText());
            S3EventNotificationRecord eventMsg = event.getRecords().get(0);

            // 強い整合性のある読み込みで保存済データを取得する
            FileMetaData currentData = dbMapper.load(FileMetaData.class,
                    eventMsg.getS3().getObject().getKey(),
                    ConsistentReads.CONSISTENT.config());

            // Create
            if (currentData == null) {
                createRecord(eventMsg);

                // Update
            } else {
                updateRecord(currentData, eventMsg);
            }

        } catch (Exception e) {
            // ommited
            e.printStackTrace();
            logger.error("メッセージ受信処理でエラーが発生。", e);
            throw new RuntimeException(e);
        }
        logger.debug("▲△▲△▲ メッセージ受信処理終了 ▲△▲△▲");
    }

    /**
     * DynamoDBに検索用レコードを登録。
     * @param eventMsg S3更新イベントメッセージ
     */
    private void createRecord(S3EventNotificationRecord eventMsg) {
        logger.debug("新規登録");

        String objectKey = eventMsg.getS3().getObject().getKey();
        String bucketName = eventMsg.getS3().getBucket().getName();
        long size = eventMsg.getS3().getObject().getSizeAsLong();
        DateTime uploadDate = eventMsg.getEventTime().withZone(DateTimeZone
                .forID("Asia/Tokyo"));
        String strUploadDate = uploadDate.toString(DateTimeFormat.forPattern(
                "yyyy-MM-dd"));
        String sequencer = eventMsg.getS3().getObject().getSequencer();

        // objectKey -> {userId, fileName}
        String[] objectKeyArr = dataSplit(objectKey);
        String uploadUser = objectKeyArr[0];
        String fileName = objectKeyArr[1];

        // create
        if (eventMsg.getEventName().contains(EV_CREATED)) {
            // データ登録
            FileMetaData newData = new FileMetaData();
            newData.setObjectKey(objectKey);
            newData.setBucketName(bucketName);
            newData.setFileName(fileName);
            newData.setSize(size);
            newData.setUploadUser(uploadUser);
            newData.setUploadDate(strUploadDate);
            newData.setSequencer(sequencer);
            dbMapper.save(newData, SaveBehavior.CLOBBER.config()); // バージョンチェック無効＆未設定項目はクリア
        }
    }

    /**
     * DynamoDBの検索用レコードを更新。
     * @param currentData DynamoDBの検索用レコードの現在登録値
     * @param eventMsg S3更新イベントメッセージ
     */
    private void updateRecord(FileMetaData currentData,
            S3EventNotificationRecord eventMsg) {
        logger.debug("更新：" + currentData.toString());

        String sequencer = eventMsg.getS3().getObject().getSequencer();

        // event sequence check
        if (!isNewEntry(currentData.getSequencer(), sequencer)) {
            logger.warn("より新しいデータが登録済のため何もしない");
            return;
        }

        String objectKey = eventMsg.getS3().getObject().getKey();
        String bucketName = eventMsg.getS3().getBucket().getName();
        // ファイルサイズを取得（削除の場合はnull->0の変換）
        long size = eventMsg.getS3().getObject().getSizeAsLong() == null ? 0L
                : eventMsg.getS3().getObject().getSizeAsLong().longValue();
        DateTime uploadDate = eventMsg.getEventTime().withZone(DateTimeZone
                .forID("Asia/Tokyo"));
        String strUploadDate = uploadDate.toString(DateTimeFormat.forPattern(
                "yyyy-MM-dd"));

        // objectKey -> {userId, fileName}
        String[] objectKeyArr = dataSplit(objectKey);
        String uploadUser = objectKeyArr[0];
        String fileName = objectKeyArr[1];

        try {
            // update
            if (eventMsg.getEventName().contains(EV_CREATED)) {
                currentData.setBucketName(bucketName);
                currentData.setFileName(fileName);
                currentData.setSize(size);
                currentData.setUploadUser(uploadUser);
                currentData.setUploadDate(strUploadDate);
                currentData.setSequencer(sequencer);
                dbMapper.save(currentData, SaveBehavior.UPDATE.config()); // バージョンチェック有効＆未設定項目は更新しない

                // delete
            } else if (eventMsg.getEventName().contains(EV_REMOVED)) {
                dbMapper.delete(currentData, SaveBehavior.UPDATE.config()); // バージョンチェック有効
            }
            // バージョンチェックエラーが発生した場合（更新対象レコードに別の更新があった場合）は
            // 更新対象レコードを再取得して更新処理をやり直す。
            // 結果，より新しい情報で更新されていた場合はsequencerを利用したチェックにより更新不要と判定される。
        } catch (ConditionalCheckFailedException e) {
            // 強い整合性のある読み込みで最新の保存済データを再取得する
            currentData = dbMapper.load(FileMetaData.class, eventMsg.getS3()
                    .getObject().getKey(), ConsistentReads.CONSISTENT.config());
            // 更新処理を再実行する
            updateRecord(currentData, eventMsg);
        }
    }

    /**
     * 現在値(curSequencer)と今回値(newSequencer)を比較して， 今回値の方が時系列的に新しいデータであることを確認する。 確認は桁数を揃えた後に辞書式比較によって大きい方を新しいデータと判定する。
     * @param curSequencer 現在値
     * @param newSequencer 今回値
     * @return true:今回値の方が新しい false:前回値の方が新しい
     */
    private boolean isNewEntry(String curSequencer, String newSequencer) {
        // 長さが短い方の右側を0埋めして，辞書式比較 compareTo
        int len = Math.abs(curSequencer.length() - newSequencer.length());

        if (len > 0) {
            String formatStr = "%0" + len + "d";
            String paddingStr = String.format(formatStr, 0);
            if (curSequencer.length() < newSequencer.length()) {
                curSequencer += paddingStr;
            } else if (newSequencer.length() < curSequencer.length()) {
                newSequencer += paddingStr;
            }
        }
        return newSequencer.compareTo(curSequencer) > 0;
    }

    /**
     * オブジェクトキー（"[UserId]-[FileName]"のフォーマットの想定）を {[UserId],[FileName]}の文字列配列に分割する。
     * @param objectKey オブジェクトキー
     * @return 分割後配列
     */
    private String[] dataSplit(String objectKey) {
        // input string is "[UserId]-[FileName]"
        if (objectKey.contains("-")) {
            int dividePos = objectKey.indexOf("-");
            return new String[] { objectKey.substring(0, dividePos), objectKey
                    .substring(dividePos + 1) };
        } else {
            return new String[] { objectKey, objectKey };
        }
    }
}
