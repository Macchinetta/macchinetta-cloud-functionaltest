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
package jp.co.ntt.cloud.functionaltest.domain.helper;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Component
public class S3Helper implements InitializingBean {

	public static final String DELIMITER = "/";
	
	public static final int STS_MIN_DURATION_MINUTES = 15;

	@Inject
	AmazonS3 s3client;

	@Value("${functionaltest.download.roleName}")
	String roleName;
	
	@Value("${functionaltest.download.roleSessionName}")
	String roleSessionName;
	
	private String roleArn;
	
	/**
	 * バケット名を指定してS3のファイル一覧を取得する。
	 * 
	 * @param bucketName
	 * @return ファイル一覧
	 */
	public List<S3ObjectSummary> listFiles(String bucketName) {
		return listFiles(bucketName, null, null);
	}

	/**
	 * バケット名、接頭辞、区切り文字を指定してS3のファイル一覧を取得する。
	 * 
	 * @param bucketName 一覧を取得するバケット
	 * @param prefix 接頭辞
	 * @param delimiter 区切り文字
	 * @return ファイル一覧
	 */
	public List<S3ObjectSummary> listFiles(String bucketName, String prefix, String delimiter) {

		if (prefix != null && !prefix.endsWith(delimiter)) {
			prefix += delimiter;
		}

		ListObjectsRequest request = new ListObjectsRequest(bucketName, prefix, null, delimiter, null);
		ObjectListing list;
		do {
			list = s3client.listObjects(request);
			request.setMarker(list.getNextMarker());
		} while (list.isTruncated());
		return list.getObjectSummaries();
	}

	/**
	 * バケット名、オブジェクトキー、有効期限を指定して署名付きURLを取得する。
	 * 
	 * @param bucketName ダウンロード対象のバケット
	 * @param key ダウンロード対象のキー
	 * @param expiration 有効期限 (秒)
	 * @return 署名付きURL
	 */
	public URL generatePresignedUrl(String bucketName, String key, Date expiration) {
		String policy = getPolicy(bucketName, key);
		AmazonS3 amazonS3 = getClient(policy);
		return amazonS3.generatePresignedUrl(bucketName, key, expiration);
	}

	/**
	 * 指定されたバケット、キーのみを GET するポリシーを取得する。
	 * 
	 * {@link Effect} に <code>Deny</code> を指定することで、
	 * <code>NotAction</code> に指定した操作、および
	 * <code>NotResource</code> に指定したリソース以外へのアクセスを明示的に禁止する。
	 * 
	 * @param bucketName ダウンロード対象のバケット
	 * @param key ダウンロード対象のキー
	 * @return 指定されたバケット、キーのみを GET するポリシー
	 */
	private String getPolicy(String bucketName, String key) {

		String resource = "arn:aws:s3:::" + bucketName + DELIMITER + key;

		Statement statement = new Statement(Statement.Effect.Allow)
				.withActions(S3Actions.GetObject)
				.withResources(new Resource(resource));

		Policy policy = new Policy().withStatements(statement);
		
		return policy.toJson();
	}
	
	/**
	 * 指定されたポリシーを持つ一時的なロールを取得する。
	 * 
	 * 署名付きURLには、そのURLを発行したロールのアクセスキー情報が含まれるため、
	 * STSにて一時的なロールを作成する。
	 * ダウンロード対象のバケットポリシーにて、この一時ロールによるアクセスが
	 * 許可されていなければならないことに注意すること。
	 * 
	 * @param policy 一時的なロールに付与するポリシー文字列
	 * @return 一時的なロール
	 */
	private AmazonS3 getClient(String policy) {
		
		int minDurationSeconds = (int) TimeUnit.MINUTES.toSeconds(STS_MIN_DURATION_MINUTES);

		STSAssumeRoleSessionCredentialsProvider credentialsProvider =
				new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, roleSessionName)
				.withRoleSessionDurationSeconds(minDurationSeconds)
				.withScopeDownPolicy(policy)
				.build();

		return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		GetRoleRequest request = new GetRoleRequest();
		request.setRoleName(roleName);

		roleArn = AmazonIdentityManagementClientBuilder.defaultClient().getRole(request).getRole().getArn();
	}
}
