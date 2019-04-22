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
package jp.co.ntt.cloud.functionaltest.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.environment.AbstractScmEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

/**
 * 環境リポジトリクラス
 * @author NTT 電電太郎
 */
@ConfigurationProperties("spring.cloud.config.server.s3")
public class S3EnvironmentRepository extends AbstractScmEnvironmentRepository
                                     implements EnvironmentRepository,
                                     SearchPathLocator, InitializingBean {

    private static Log logger = LogFactory.getLog(
            S3EnvironmentRepository.class);

    public S3EnvironmentRepository(ConfigurableEnvironment environment) {
        super(environment);
    }

    /**
     * 対象となる環境リポジトリから、ファイルをダウンロードおよびチェックアウトして、ローカルの一時ディレクトリに保存して、ロケーションを返却する。
     * @param application アプリケーション名
     * @param profile プロファイル名
     * @param label ラベル名
     * @return 環境依存値ファイルのロケーション
     */
    @Override
    public synchronized Locations getLocations(String application,
            String profile, String label) {

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();
        TransferManager tm = null;
        try {
            String bucketName = new AmazonS3URI(getUri()).getBucket();
            logger.info("bucket name:" + bucketName);

            tm = TransferManagerBuilder.standard().withS3Client(amazonS3)
                    .build();
            logger.info("local temp dir:" + getBasedir().getAbsolutePath());
            MultipleFileDownload download = tm.downloadDirectory(bucketName,
                    null, getBasedir());
            download.waitForCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            throw new IllegalStateException("Cannot download s3", t);
        } finally {
            if (tm != null) {
                tm.shutdownNow();
            }
        }

        return new Locations(application, profile, label, null, getSearchLocations(
                getWorkingDirectory(), application, profile, label));
    }

    /**
     * S3 URI を検証
     */
    @Override
    public void afterPropertiesSet() {
        Assert.state(getUri() != null,
                "You need to configure a uri for the s3 bucket (e.g. 's3://bucket/')");
        // S3 URIを検証するためにインスタンス化
        new AmazonS3URI(getUri());
    }

}
