/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.domain.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * プロパティファイルから取得したS3関連の情報を保持するDtoクラス
 * <p>
 * {@link @ConfigurationProperties}で取得するパターン
 *
 * @author NTT 電電太郎
 *
 */
@Component
@ConfigurationProperties(prefix = "upload")
public class S3ConfigConfigurationPropertiesDto {
    /**
     * S3バケット名。
     */
    private String bucketname;

    /**
     * ファイル一時保存ディレクトリ。
     */
    private String temproryDirectory;

    /**
     * ファイル保存ディレクトリ。
     */
    private String saveDirectory;

    /**
     * S3バケット名を取得する。
     *
     * @return S3バケット名
     */
    public String getBucketname() {
        return bucketname;
    }

    /**
     * S3バケット名を設定する。
     *
     * @param bucketname
     *            S3バケット名
     *
     */
    public void setBucketname(String bucketname) {
        this.bucketname = bucketname;
    }

    /**
     * ファイル一時保存ディレクトリを取得する。
     *
     * @return ファイル一時保存ディレクトリ
     */
    public String getTemproryDirectory() {
        return temproryDirectory;
    }

    /**
     * ファイル一時保存ディレクトリを設定する。
     *
     * @param temproryDirectory
     *            ファイル一時保存ディレクトリ
     */
    public void setTemproryDirectory(String temproryDirectory) {
        this.temproryDirectory = temproryDirectory;
    }

    /**
     * ファイル保存ディレクトリを取得する。
     *
     * @return ファイル保存ディレクトリ
     */
    public String getSaveDirectory() {
        return saveDirectory;
    }

    /**
     * ファイル保存ディレクトリを設定する。
     *
     * @param saveDirectory
     *            ファイル保存ディレクトリ
     */
    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

}
