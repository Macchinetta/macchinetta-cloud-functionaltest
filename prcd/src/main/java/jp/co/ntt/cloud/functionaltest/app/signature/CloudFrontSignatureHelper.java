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
package jp.co.ntt.cloud.functionaltest.app.signature;

import java.io.File;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.terasoluna.gfw.common.exception.SystemException;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.CloudFrontCookieSigner.CookiesForCustomPolicy;
import com.amazonaws.services.cloudfront.util.SignerUtils.Protocol;

/**
 * クラウドフロント署名ヘルパー
 * @author NTT 電電太郎
 */
@ConfigurationProperties(prefix = "functionaltest.cf.signature")
public class CloudFrontSignatureHelper {

    /**
     * プロトコル。
     */
    private com.amazonaws.Protocol protocol = com.amazonaws.Protocol.HTTPS;

    /**
     * セキュア。
     */
    private boolean secure = true;

    /**
     * ドメイン。
     */
    @NotEmpty
    private String domain;

    /**
     * クッキーパス
     */
    @NotEmpty
    private String cookiePath;

    /**
     * ディストリビューションドメイン。
     */
    @NotEmpty
    private String distributionDomain;

    /**
     * プライベートキーファイルパス。
     */
    @NotEmpty
    private String privateKeyFilePath;

    /**
     * リソースパス。
     */
    @NotEmpty
    private String resourcePath;

    /**
     * キーペアID(アクセスキーID)。
     */
    @NotEmpty
    private String keyPairId;

    /**
     * 有効期限開始までの時間（分）。
     */
    private Integer timeToActive;

    /**
     * 有効期限終了までの時間（分）。
     */
    @Min(1)
    private int timeToExpire;

    /**
     * 許可するIPアドレスの範囲（CIDR）。
     */
    private String allowedIpRange;

    /**
     * カスタムポリシーによって作成されたクッキー情報を返却する。
     * @return クッキー
     */
    public CookiesForCustomPolicy getSignedCookies() {

        // プロトコルの設定
        Protocol signerUtilsProtocol = Protocol.valueOf(protocol.toString());

        // プライベートキーファイルの設定
        File privateKeyFile = new File(privateKeyFilePath);

        // 有効期限の設定
        // 有効期間：開始
        Date activeFrom = getPlusMinutesFromCurrentTime(timeToActive);

        // 有効期間:終了
        Date expiresOn = getPlusMinutesFromCurrentTime(timeToExpire);

        // cookie作成
        CookiesForCustomPolicy cookies = null;
        try {
            cookies = CloudFrontCookieSigner.getCookiesForCustomPolicy(
                    signerUtilsProtocol, distributionDomain, privateKeyFile,
                    resourcePath, keyPairId, expiresOn, activeFrom,
                    allowedIpRange);
        } catch (IOException e) {
            throw new SystemException("e.xx.fw.9001", "I/O error occured.", e);
        } catch (InvalidKeySpecException e) {
            throw new SystemException("e.xx.fw.9001", "invalid key specification.", e);
        }
        return cookies;
    }

    private Date getPlusMinutesFromCurrentTime(Integer minutes) {
        if (minutes == null) {
            return null;
        }
        DateTime currentTime = new DateTime(DateTimeZone.UTC);
        return currentTime.plusMinutes(minutes).toDate();
    }

    public com.amazonaws.Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(com.amazonaws.Protocol protocol) {
        this.protocol = protocol;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getDistributionDomain() {
        return distributionDomain;
    }

    public void setDistributionDomain(String distributionDomain) {
        this.distributionDomain = distributionDomain;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public void setKeyPairId(String keyPairId) {
        this.keyPairId = keyPairId;
    }

    public Integer getTimeToActive() {
        return timeToActive;
    }

    public void setTimeToActive(Integer timeToActive) {
        this.timeToActive = timeToActive;
    }

    public int getTimeToExpire() {
        return timeToExpire;
    }

    public void setTimeToExpire(int timeToExpire) {
        this.timeToExpire = timeToExpire;
    }

    public String getAllowedIpRange() {
        return allowedIpRange;
    }

    public void setAllowedIpRange(String allowedIpRange) {
        this.allowedIpRange = allowedIpRange;
    }


}
