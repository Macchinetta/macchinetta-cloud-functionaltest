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

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;

/**
 * ファイルアップロードサービス実装クラス。
 * 
 * @author NTT 電電太郎
 */
@Service
public class SearchSharedServiceImpl implements SearchSharedService {

	@Inject
	DynamoDBMapper dbMapper;

	/**
	 * 全ファイル検索を実行する。
	 */
	public List<FileMetaData> doSearch() {
		List<FileMetaData> result = dbMapper.scan(FileMetaData.class, new DynamoDBScanExpression());
		return result;
	}

	/**
	 * PKによるファイル検索を実行する。
	 * 
	 * @param objectKey オブジェクトキー
	 */
	public FileMetaData doPkSearch(String objectKey) {
		FileMetaData result = dbMapper.load(FileMetaData.class, objectKey);
		return result;
	}

	/**
	 * グローバルセカンダリインデックス（アップロードユーザ,アップロード日付指定）によるファイル検索を実行する。
	 * 
	 * @param uploadUser アップロードユーザ
	 * @param uploadDate アップロード日付
	 */
	public List<FileMetaData> doUserIdIndexSearch(String uploadUser, String uploadDate) {
		
		// グローバルセカンダリインデックスの読み込み
		HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(uploadUser));
		String keyConditionExpression = "uploadUser = :v1";
		
		if (uploadDate.length() > 0) {
			eav.put(":v2", new AttributeValue().withS(uploadDate));
			keyConditionExpression += " and uploadDate = :v2";
		}
		
		DynamoDBQueryExpression<FileMetaData> queryExpression = new DynamoDBQueryExpression<FileMetaData>()
				.withIndexName("uploadUser-uploadDate-index")
				.withConsistentRead(false)  // グローバルセカンダリインデックスの場合は強い整合性のある読み込みをサポートしないためfalseを指定
				.withKeyConditionExpression(keyConditionExpression)
				.withExpressionAttributeValues(eav);
		List<FileMetaData> indexResult =  dbMapper.query(FileMetaData.class, queryExpression);
		for (FileMetaData data : indexResult) {
			System.out.println("インデックス検索:"+ data.toString());
		}
		return indexResult;
	}

	/**
	 * グローバルセカンダリインデックス（バケット名指定）によるファイル検索を実行する。
	 * 
	 * @param bucketName バケット名
	 */
	public List<FileMetaData> doBucketNameIndexSearch(String bucketName) {
		
		// グローバルセカンダリインデックスの読み込み
		HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(bucketName));
		
		DynamoDBQueryExpression<FileMetaData> queryExpression = new DynamoDBQueryExpression<FileMetaData>()
				.withIndexName("bucketName-size-index")
				.withConsistentRead(false)  // グローバルセカンダリインデックスの場合は強い整合性のある読み込みをサポートしないためfalseを指定
				.withKeyConditionExpression("bucketName = :v1")
				.withScanIndexForward(false)
				.withExpressionAttributeValues(eav);
		List<FileMetaData> indexResult =  dbMapper.query(FileMetaData.class, queryExpression);
		for (FileMetaData data : indexResult) {
			System.out.println("インデックス検索:"+ data.toString());
		}
		return indexResult;
	}
}
