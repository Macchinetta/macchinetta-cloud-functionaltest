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
package jp.co.ntt.cloud.functionaltest.domain.service.fileupload;

import org.springframework.web.multipart.MultipartFile;

/**
 * ファイルアップロードサービスインタフェース。
 * @author NTT 電電太郎
 */
public interface MaintenanceService {

	/**
	 * S3へのファイルアップロードを実行する。
	 * 
	 * @param uploadUser アップロードユーザ（ファイル命名用）
	 * @param bucketName バケット名
	 * @param uploadFile アップロードファイル
	 */
	public void doUpload(String uploadUser, String bucketName, MultipartFile uploadFile);

	/**
	 * S3からのファイル削除を実行する。
	 * 
	 * @param bucketName バケット名
	 * @param objectKey オブジェクトキー
	 * @param uploadUser アップロードユーザ
	 */
	public void doDelete(String bucketName, String objectKey, String uploadUser);

}
