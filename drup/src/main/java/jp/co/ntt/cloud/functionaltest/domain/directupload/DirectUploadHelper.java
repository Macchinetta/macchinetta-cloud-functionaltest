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
package jp.co.ntt.cloud.functionaltest.domain.directupload;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.stereotype.Component;
import org.terasoluna.gfw.common.exception.SystemException;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.ntt.cloud.functionaltest.domain.common.constants.LogMessageConstants;
import jp.co.ntt.cloud.functionaltest.domain.service.login.SampleUserDetails;

/**
 * ダイレクトアップロードヘルパー
 * @author NTT 電電太郎
 */
@Component
public class DirectUploadHelper implements InitializingBean {

    /**
     * 認証情報の最小有効時間(分)
     */
    private static final int STS_MIN_DURATION_MINUTES = 15;

    /**
     * 引受ロールのロール名
     */
    @Value("${functionaltest.upload.roleName}")
    String roleName;

    /**
     * AssumeRoleリクエスト時のセッション名
     */
    @Value("${functionaltest.upload.roleSessionName}")
    String roleSessionName;

    /**
     * POSTポリシーの有効期間(秒)
     */
    @Value("${functionaltest.upload.durationseconds:30}")
    int durationSeconds;

    /**
     * アップロードファイルサイズ上限(バイト)
     */
    @Value("${functionaltest.upload.limitBytes}")
    String fileSizeLimit;

    /**
     * S3バケットのプレフィックス
     */
    @Value("${functionaltest.upload.prefix}")
    String s3prefix;

    /**
     * オブジェクトマッパー
     */
    @Inject
    ObjectMapper objectMapper;

    /**
     * リージョンプロバイダ
     */
    @Inject
    RegionProvider regionProvider;

    /**
     * ロールARN
     */
    private String roleArn;

    /**
     * ダイレクトアップロードに必要な認証情報を返却する。
     * @param bucketName バケット名
     * @param fileName ファイル名
     * @param userDetails ログインユーザー情報
     * @return 認証情報オブジェクト
     */
    public DirectUploadAuthInfo getDirectUploadInfo(String bucketName,
            String fileName, SampleUserDetails userDetails) {

        // オブジェクトキーを生成する
        String objectKey = s3prefix + createObjectKey(userDetails);

        // STSから一時的認証情報を取得する
        Credentials credentials = getTemporaryCredentials(bucketName,
                objectKey);

        String regionName = regionProvider.getRegion().getName();
        String serviceName = "s3";
        DateTime nowUTC = new DateTime(DateTimeZone.UTC);
        String date = nowUTC.toString("yyyyMMdd");
        String acl = "private";
        String credentialString = credentials.getAccessKeyId() + "/" + date
                + "/" + regionName + "/" + serviceName + "/" + "aws4_request";
        String securityToken = credentials.getSessionToken();
        String s3endpoint = "s3-" + regionName + ".amazonaws.com";
        String targetUrl = "https://" + bucketName + "." + s3endpoint + "/";
        String algorithm = "AWS4-HMAC-SHA256";
        String iso8601dateTime = nowUTC.toString("yyyyMMdd'T'HHmmss'Z'");

        // POSTポリシーの作成
        // @formatter:off
        PostPolicy postPolicy = new PostPolicy();
        postPolicy.setExpiration(nowUTC.plusSeconds(durationSeconds)
                .toString());
        postPolicy.setConditions(new String[][] { { "eq", "$bucket",
                bucketName }, { "eq", "$key", objectKey }, { "eq", "$acl",
                        acl }, { "eq", "$x-amz-meta-filename", fileName }, {
                                "eq", "$x-amz-credential", credentialString }, {
                                        "eq", "$x-amz-security-token",
                                        securityToken }, { "eq",
                                                "$x-amz-algorithm", algorithm },
                { "eq", "$x-amz-date", iso8601dateTime }, {
                        "content-length-range", "0", fileSizeLimit } });
        // @formatter:on

        // JavaオブジェクトからJSONドキュメントへ変換する
        String policyDocument = null;
        try {
            policyDocument = objectMapper.writeValueAsString(postPolicy);
        } catch (JsonProcessingException e) {
            throw new SystemException(LogMessageConstants.MSG_ID_9001, e
                    .getMessage(), e);
        }

        // POSTポリシーをBASE64エンコーディング
        String base64policy = Base64.getEncoder().encodeToString(policyDocument
                .getBytes(StandardCharsets.UTF_8));

        // 認証情報を元に、署名バージョン4の署名キーを生成する
        byte[] signingKey = getSignatureKey(credentials.getSecretAccessKey(),
                date, regionName, serviceName);

        // POSTポリシーに対して署名計算を行う
        String signatureForPolicy = Hex.encodeHexString(calculateHmacSHA256(
                base64policy, signingKey));

        // クライアント返却用のオブジェクトに各情報を設定する
        DirectUploadAuthInfo res = new DirectUploadAuthInfo();
        res.setTargetUrl(targetUrl);
        res.setAcl(acl);
        res.setDate(iso8601dateTime);
        res.setObjectKey(objectKey);
        res.setSecurityToken(securityToken);
        res.setAlgorithm(algorithm);
        res.setCredential(credentialString);
        res.setSignature(signatureForPolicy);
        res.setPolicy(base64policy);
        res.setRawFileName(fileName);
        res.setFileSizeLimit(fileSizeLimit);

        return res;
    }

    private String createObjectKey(SampleUserDetails userDetails) {
        String userId = userDetails.getUsername();
        return userId + "/" + UUID.randomUUID();
    }

    private Credentials getTemporaryCredentials(String bucketName,
            String objectKey) {

        String resourceArn = "arn:aws:s3:::" + bucketName + "/" + objectKey;

        // 一時的セキュリティ認証情報に適用するIAMポリシーを指定する。
        // アップロード対象のオブジェクトにのみPutObjectリクエストが可能なユーザとなる。
        // @formatter:off
        Statement statement = new Statement(Statement.Effect.Allow).withActions(
                S3Actions.PutObject).withResources(new Resource(resourceArn));
        // @formatter:on

        String iamPolicy = new Policy().withStatements(statement).toJson();

        int minDurationSeconds = (int) TimeUnit.MINUTES.toSeconds(
                STS_MIN_DURATION_MINUTES);

        // STSに対してAssumeRoleリクエストを発行し、一時的セキュリティ認証情報を取得する
        // @formatter:off
        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                .withRoleArn(roleArn).withDurationSeconds(minDurationSeconds)
                .withRoleSessionName(roleSessionName).withPolicy(iamPolicy);
        // @formatter:on

        return AWSSecurityTokenServiceClientBuilder.defaultClient().assumeRole(
                assumeRoleRequest).getCredentials();
    }

    private byte[] getSignatureKey(String key, String dateStamp, String region,
            String serviceName) {
        // 署名バージョン4の署名キーを生成する
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = calculateHmacSHA256(dateStamp, kSecret);
        byte[] kRegion = calculateHmacSHA256(region, kDate);
        byte[] kService = calculateHmacSHA256(serviceName, kRegion);
        byte[] kSigning = calculateHmacSHA256("aws4_request", kService);
        return kSigning;
    }

    private byte[] calculateHmacSHA256(String stringToSign, byte[] signingKey) {
        String algorithm = "HmacSHA256";
        Mac mac = null;
        try {
            mac = Mac.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException(LogMessageConstants.MSG_ID_9001, e
                    .getMessage(), e);
        }
        try {
            mac.init(new SecretKeySpec(signingKey, algorithm));
        } catch (InvalidKeyException e) {
            throw new SystemException(LogMessageConstants.MSG_ID_9001, e
                    .getMessage(), e);
        }

        return mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // ロール名からロールARNを取得する。
        GetRoleRequest request = new GetRoleRequest();
        request.setRoleName(roleName);

        roleArn = AmazonIdentityManagementClientBuilder.defaultClient().getRole(
                request).getRole().getArn();
    }

    private class PostPolicy {

        private String expiration;

        private String[][] conditions;

        @SuppressWarnings("unused")
        public String getExpiration() {
            return expiration;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        @SuppressWarnings("unused")
        public String[][] getConditions() {
            return conditions;
        }

        public void setConditions(String[][] conditions) {
            this.conditions = conditions;
        }
    }
}
