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

import java.util.List;

import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;

/**
 * ファイルアップロードサービスインタフェース。
 * 
 * @author NTT 電電太郎
 */
public interface SearchSharedService {

	/**
	 * 全ファイル検索を実行する。
	 */
	public List<FileMetaData> doSearch();

	/**
	 * PKによるファイル検索を実行する。
	 * 
	 * @param objectKey オブジェクトキー
	 */
	public FileMetaData doPkSearch(String objectKey);

	/**
	 * グローバルセカンダリインデックス（アップロードユーザ,アップロード日付指定）によるファイル検索を実行する。
	 * 
	 * @param uploadUser アップロードユーザ
	 * @param uploadDate アップロード日付
	 */
	public List<FileMetaData> doUserIdIndexSearch(String uploadUser, String uploadDate);

	/**
	 * グローバルセカンダリインデックス（バケット名指定）によるファイル検索を実行する。
	 * 
	 * @param bucketName バケット名
	 */
	public List<FileMetaData> doBucketNameIndexSearch(String bucketName);
}
