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
package jp.co.ntt.cloud.functionaltest.domain.service.fileupload;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;

/**
 * ファイルアップロードサービス実装クラス。
 * @author NTT 電電太郎
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

	@Inject
	DynamoDBMapper dbMapper;
	
	@Inject
	SearchSharedService searchSharedService;

	@Inject
	AmazonS3 s3client;
	
	/**
	 * S3へのファイルアップロードを実行する。
	 * 
	 * @param uploadUser アップロードユーザ（ファイル命名用）
	 * @param bucketName バケット名
	 * @param uploadFile アップロードファイル
	 */
	public void doUpload(String uploadUser, String bucketName, MultipartFile uploadFile) {
		try {
			// アップロードファイルからファイル名フルパスを取得し，ファイル名部分のみを取り出す
			String fileName = uploadFile.getOriginalFilename();
			fileName = fileName.substring(fileName.lastIndexOf("\\")+1);

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(uploadFile.getBytes().length);

			s3client.putObject(new PutObjectRequest(
					bucketName, 
					uploadUser +"-"+ fileName, 
					uploadFile.getInputStream(), metadata));

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * S3からのファイル削除を実行する。
	 * 
	 * @param bucketName バケット名
	 * @param objectKey オブジェクトキー
	 * @param uploadUser アップロードユーザ
	 */
	public void doDelete(String bucketName, String objectKey, String uploadUser) {
		// バケット名とオブジェクトキーが指定された場合は単一ファイルを削除する。
		if (bucketName.length()>0 && objectKey.length()>0) {
			s3client.deleteObject(new DeleteObjectRequest(bucketName, objectKey));
			
		// ユーザIDが指定された場合は複数ファイルを削除する。
		} else if (uploadUser.length()>0) {
			List<FileMetaData> tgtList = searchSharedService.doUserIdIndexSearch(uploadUser, "");
			for (FileMetaData tgt : tgtList) {
				s3client.deleteObject(new DeleteObjectRequest(tgt.getBucketName(), tgt.getObjectKey()));
			}
		}
	}
}
