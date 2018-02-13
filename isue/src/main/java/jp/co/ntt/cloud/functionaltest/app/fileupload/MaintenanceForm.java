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
package jp.co.ntt.cloud.functionaltest.app.fileupload;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

public class MaintenanceForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 登録：ファイル */
	private MultipartFile uFile;
	/** 登録：アップロードユーザ */
	@Size(min = 0, max = 100)
	private String uUploadUser;
	/** 登録：バケット名 */
	@Size(min = 0, max = 100)
	private String uBucketName;
	
	/** 削除：バケット名 */
	@Size(min = 0, max = 100)
	private String dBucketName;
	/** 削除：オブジェクトキー */
	@Size(min = 0, max = 100)
	private String dObjectKey;
	/** 削除：アップロードユーザ */
	@Size(min = 0, max = 100)
	private String dUploadUser;


	public MultipartFile getuFile() {
		return uFile;
	}
	public void setuFile(MultipartFile uFile) {
		this.uFile = uFile;
	}

	public String getuUploadUser() {
		return uUploadUser;
	}
	public void setuUploadUser(String uUploadUser) {
		this.uUploadUser = uUploadUser;
	}

	public String getuBucketName() {
		return uBucketName;
	}
	public void setuBucketName(String uBucketName) {
		this.uBucketName = uBucketName;
	}

	public String getdBucketName() {
		return dBucketName;
	}
	public void setdBucketName(String dBucketName) {
		this.dBucketName = dBucketName;
	}

	public String getdObjectKey() {
		return dObjectKey;
	}
	public void setdObjectKey(String dObjectKey) {
		this.dObjectKey = dObjectKey;
	}

	public String getdUploadUser() {
		return dUploadUser;
	}
	public void setdUploadUser(String dUploadUser) {
		this.dUploadUser = dUploadUser;
	}
}
