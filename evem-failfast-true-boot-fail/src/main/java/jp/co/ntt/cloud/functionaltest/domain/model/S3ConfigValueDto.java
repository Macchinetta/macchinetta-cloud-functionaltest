/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.domain.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * プロパティファイルから取得したS3関連の情報を保持するDtoクラス
 * <p>
 * {@link @Value}で取得するパターン
 * @author NTT 電電太郎
 */
@Component
public class S3ConfigValueDto {
    /**
     * S3バケット名。
     */
    @Value("${upload.bucketname}")
    private String bucketname;

    /**
     * ファイル一時保存ディレクトリ。
     */
    @Value("${upload.temproryDirectory}")
    private String temproryDirectory;

    /**
     * ファイル保存ディレクトリ。
     */
    @Value("${upload.saveDirectory}")
    private String saveDirectory;

    /**
     * S3バケット名を取得する。
     * @return S3バケット名
     */
    public String getBucketname() {
        return bucketname;
    }

    /**
     * ファイル一時保存ディレクトリを取得する。
     * @return ファイル一時保存ディレクトリ
     */
    public String getTemproryDirectory() {
        return temproryDirectory;
    }

    /**
     * ファイル保存ディレクトリを取得する。
     * @return ファイル保存ディレクトリ
     */
    public String getSaveDirectory() {
        return saveDirectory;
    }
}
