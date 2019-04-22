/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.domain.service.getconfig;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import jp.co.ntt.cloud.functionaltest.domain.model.S3ConfigConfigurationPropertiesDto;
import jp.co.ntt.cloud.functionaltest.domain.model.S3ConfigValueDto;

/**
 * S3のバケット名、ファイル一時保存ディレクトリ、ファイル保存ディレクトリを取得するサービスクラス
 * @author NTT 電電太郎
 */
@Service
public class GetS3ConfigService {

    @Inject
    S3ConfigConfigurationPropertiesDto s3ConfigConfigurationPropertiesDto;

    @Inject
    S3ConfigValueDto s3ConfigConfigValueDto;

    /**
     * プロパティファイルから{@link @ConfigurationProperties}で取得したS3関連の情報を保持するDtoクラスを取得する。
     * @return プロパティファイルから取得したS3関連の情報(@ConfigurationPropertiesバージョン)
     */
    public S3ConfigConfigurationPropertiesDto getConfigurationPropertiesDto() {
        return s3ConfigConfigurationPropertiesDto;
    }

    /**
     * プロパティファイルから{@link @Value}で取得したS3関連の情報を保持するDtoクラスを取得する。
     * @return プロパティファイルから取得したS3関連の情報(@Valueバージョン)
     */
    public S3ConfigValueDto getConfigValueDto() {
        return s3ConfigConfigValueDto;
    }
}
