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
package jp.co.ntt.cloud.functionaltest.domain.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.terasoluna.gfw.common.exception.SystemException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

/**
 * ファイルアップロード処理を行うヘルパークラス
 * @author NTT 電電太郎
 */
@Component
public class FileUploadHelper {

    /**
     * リソースローダ。
     */
    @Inject
    ResourceLoader resourceLoader;

    /**
     * S3クライアント。
     */
    @Inject
    AmazonS3 s3client;

    /**
     * リソースパターンリゾルバ。
     */
    @Inject
    ResourcePatternResolver resourcePatternResolver;

    /**
     * 単一ファイルの一時ファイルアップロードを行う。
     * @param uploadFile アップロードファイル
     * @param bucketName バケット名
     * @param destDir アップロードディレクトリ
     * @return アップロードしたファイルのオブジェクトキー
     */
    public String uploadTempFile(MultipartFile uploadFile, String bucketName,
            String destDir) {

        // アップロードディレクトリ名 + "/" + ファイル名 をオブジェクトキーとする。
        String objectKey = destDir + "uploadFile_" + UUID.randomUUID()
                .toString() + ".jpg";
        WritableResource resource = (WritableResource) getResource(bucketName,
                objectKey);
        try (InputStream inputStream = uploadFile.getInputStream();
                OutputStream outputStream = resource.getOutputStream()) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new SystemException("e.xx.fw.9001", "i/o errors occurred.", e);
        }
        return objectKey;
    }

    private Resource getResource(String bucketName, String objectKey) {
        return this.resourceLoader.getResource("s3://" + bucketName + "/"
                + objectKey);
    }

    /**
     * 単一ファイルの削除を行う。
     * @param bucketName バケット名
     * @param objectKey 削除対象オブジェクトキー
     */
    public void deleteObject(String bucketName, String objectKey) {
        s3client.deleteObject(bucketName, objectKey);
    }

    /**
     * 複数ファイルの削除を行う。
     * @param bucketName バケット名
     * @param objectKeyList 削除対象キーリスト
     */
    public void deleteObjects(String bucketName, List<String> objectKeyList) {

        List<KeyVersion> targetKeys = new ArrayList<>();

        for (String objectKey : objectKeyList) {
            targetKeys.add(new KeyVersion(objectKey));
        }

        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
        request.setKeys(targetKeys);
        s3client.deleteObjects(request);
    }

    /**
     * オブジェクトの検索を行う
     * @param bucketName バケット名
     * @param pattern 検索条件
     */
    public Resource[] searchResource(String bucketName, String pattern) {
        try {
            return resourcePatternResolver.getResources("s3://" + bucketName
                    + "/" + pattern);
        } catch (IOException e) {
            throw new SystemException("e.xx.fw.9001", "i/o errors occurred.", e);
        }
    }
}
