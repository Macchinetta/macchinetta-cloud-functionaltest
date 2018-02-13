/*
 * Copyright 2014-2018 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.domain.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * プロパティファイルから取得したS3関連の情報を保持するDtoクラス
 * <p>
 * {@link @Value}で取得するパターン
 *
 * @author NTT 電電太郎
 *
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
     *
     * @return S3バケット名
     */
    public String getBucketname() {
        return bucketname;
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
     * ファイル保存ディレクトリを取得する。
     *
     * @return ファイル保存ディレクトリ
     */
    public String getSaveDirectory() {
        return saveDirectory;
    }
}
