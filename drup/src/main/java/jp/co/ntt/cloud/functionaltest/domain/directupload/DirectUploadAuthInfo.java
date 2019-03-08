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
 *
 */
package jp.co.ntt.cloud.functionaltest.domain.directupload;

/**
 * ダイレクトアップロード用認証情報オブジェクト
 * @author NTT 電電 太郎
 */
public class DirectUploadAuthInfo {

    /**
     * アップロード先URL
     */
    private String targetUrl;

    /**
     * アップロードファイルACL
     */
    private String acl;

    /**
     * POSTポリシー
     */
    private String policy;

    /**
     * 一時的セキュリティ認証情報のセキュリティトークン
     */
    private String securityToken;

    /**
     * アップロード対象のオブジェクトキー
     */
    private String objectKey;

    /**
     * 日付
     */
    private String date;

    /**
     * 署名アルゴリズム
     */
    private String algorithm;

    /**
     * 認証情報
     */
    private String credential;

    /**
     * 署名
     */
    private String signature;

    /**
     * 元ファイル名
     */
    private String rawFileName;

    /**
     * ファイルサイズ上限
     */
    private String fileSizeLimit;

    /**
     * @return targetUrl
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * @param targetUrl セットする targetUrl
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * @return acl
     */
    public String getAcl() {
        return acl;
    }

    /**
     * @param acl セットする acl
     */
    public void setAcl(String acl) {
        this.acl = acl;
    }

    /**
     * @return policy
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * @param policy セットする policy
     */
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    /**
     * @return securityToken
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * @param securityToken セットする securityToken
     */
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    /**
     * @return objectKey
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * @param objectKey セットする objectKey
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date セットする date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @param algorithm セットする algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * @return credential
     */
    public String getCredential() {
        return credential;
    }

    /**
     * @param credential セットする credential
     */
    public void setCredential(String credential) {
        this.credential = credential;
    }

    /**
     * @return signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature セットする signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return rawFileName
     */
    public String getRawFileName() {
        return rawFileName;
    }

    /**
     * @param rawFileName セットする rawFileName
     */
    public void setRawFileName(String rawFileName) {
        this.rawFileName = rawFileName;
    }

    /**
     * @return fileSizeLimit
     */
    public String getFileSizeLimit() {
        return fileSizeLimit;
    }

    /**
     * @param fileSizeLimit セットする fileSizeLimit
     */
    public void setFileSizeLimit(String fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }
}
