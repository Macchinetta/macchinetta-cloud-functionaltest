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
package jp.co.ntt.cloud.functionaltest.app.directupload;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.ntt.cloud.functionaltest.domain.directupload.DirectUploadHelper;
import jp.co.ntt.cloud.functionaltest.domain.directupload.DirectUploadAuthInfo;
import jp.co.ntt.cloud.functionaltest.domain.service.login.SampleUserDetails;

/**
 * ダイレクトアップロードコントローラ。
 * @author NTT 電電太郎
 */
@Controller
@RequestMapping("upload")
public class DirectUploadController {

    /**
     * S3バケット。
     */
    @Value("${functionaltest.upload.bucketName}")
    String directUploadBucketName;

    /**
     * DirectUploadHelper。
     */
    @Inject
    DirectUploadHelper directUploadHelper;

    /**
     * アップロード画面を表示する。
     * @return View論理名
     */
    @GetMapping
    public String upload() {
        return "upload/index";
    }

    /**
     * ダイレクトアップロードに必要な認証情報を取得する。
     * @param fileName ファイル名
     * @return ダイレクトアップロード用認証情報
     */
    @GetMapping(params = "info")
    @ResponseBody
    public DirectUploadAuthInfo getInfoForDirectUpload(
            @RequestParam("filename") String fileName,
            @AuthenticationPrincipal SampleUserDetails userDetails) {

        return directUploadHelper.getDirectUploadInfo(directUploadBucketName,
                fileName, userDetails);
    }
}
