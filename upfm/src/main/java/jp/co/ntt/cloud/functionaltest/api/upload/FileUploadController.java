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
package jp.co.ntt.cloud.functionaltest.api.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.upload.FileUploadHelper;

/**
 * ファイルアップロード機能試験用コントローラー
 * @author NTT 電電太郎
 */
@RestController
public class FileUploadController {

    /**
     * S3バケット。
     */
    @Value("${functionaltest.upload.bucketName}")
    private String bucketName;

    /**
     * ファイル一時保存ディレクトリ。
     */
    @Value("${functionaltest.upload.tmpDirectory}")
    private String tmpDirectory;

    /**
     * ファイルアップロードヘルパー
     */
    @Inject
    FileUploadHelper helper;

    /**
     * ファイルをアップロードする。
     * @param multipartFile アップロードするファイル
     * @return オブジェクトキー
     */
    @PostMapping(value = WebPagePathConstants.FILE)
    public String uploadFile(
            @RequestParam("file") MultipartFile multipartFile) {
        return helper.uploadTempFile(multipartFile, bucketName, tmpDirectory);
    }

    /**
     * ファイルを削除する
     * @param objectKey 削除対象オブジェクトキー
     */
    @DeleteMapping(value = WebPagePathConstants.FILE, params = "objectkey")
    public void deleteFile(@RequestParam("objectkey") String objectKey) {
        helper.deleteObject(bucketName, objectKey);
    }

    /**
     * 複数ファイルを削除する
     * @param objectKeys 削除対象オブジェクトキー配列
     */
    @DeleteMapping(value = WebPagePathConstants.FILE, params = "objectkeys")
    public void deleteFiles(@RequestParam("objectkeys") String[] objectKeys) {
        helper.deleteObjects(bucketName, Arrays.asList(objectKeys));
    }

    /**
     * ファイルを検索する
     * @param pattern 検索パターン文字列
     * @return 検索結果配列
     */
    @GetMapping(value = WebPagePathConstants.FILE)
    public String[] searchFiles(@RequestParam("pattern") String pattern) {
        Resource[] resources = helper.searchResource(bucketName, pattern);

        List<String> list = new ArrayList<>();
        for (Resource resource : resources) {
            list.add(resource.getFilename());
        }
        return list.toArray(new String[resources.length]);
    }

}
