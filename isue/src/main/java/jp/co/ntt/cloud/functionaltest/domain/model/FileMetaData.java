/*
 * Copyright 2014-2020 NTT Corporation.
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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

/**
 * ファイルメタデータ。
 * @author NTT 電電太郎
 */
@DynamoDBTable(tableName = "FuncFileMetaData")
public class FileMetaData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** オブジェクトキー */
    private String objectKey;

    /** バケット名 */
    private String bucketName;

    /** ファイル名 */
    private String fileName;

    /** サイズ */
    private long size;

    /** 登録ユーザ */
    private String uploadUser;

    /** 登録日時 */
    private String uploadDate;

    /** シーケンサ */
    private String sequencer;

    /** バージョン */
    private Long version;

    @DynamoDBHashKey
    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "bucketName-size-index")
    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @DynamoDBAttribute
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "bucketName-size-index")
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "uploadUser-uploadDate-index")
    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "uploadUser-uploadDate-index")
    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @DynamoDBAttribute
    public String getSequencer() {
        return sequencer;
    }

    public void setSequencer(String sequencer) {
        this.sequencer = sequencer;
    }

    @DynamoDBVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FileMetaData org = (FileMetaData) o;

        return objectKey.equals(org.objectKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return objectKey.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileMetaData [");
        sb.append("objectKey=").append(objectKey).append(", ");
        sb.append("bucketName=").append(bucketName).append(", ");
        sb.append("fileName=").append(fileName).append(", ");
        sb.append("size=").append(size).append(", ");
        sb.append("uploadUser=").append(uploadUser).append(", ");
        sb.append("uploadDate=").append(uploadDate).append(", ");
        sb.append("sequencer=").append(sequencer).append(", ");
        sb.append("version=").append(version).append(", ");
        sb.append("]");
        return sb.toString();
    }

}
