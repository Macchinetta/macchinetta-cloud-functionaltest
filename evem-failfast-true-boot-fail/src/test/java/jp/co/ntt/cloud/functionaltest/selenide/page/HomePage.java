/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.selenide.page;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/**
 * Homeページのページオブジェクトクラス。
 */
public class HomePage {

    private SelenideElement s3ConfigConfigurationPropertiesTable;

    private SelenideElement s3ConfigValueTable;

    /**
     * HomePageのコンストラクタ。
     */
    public HomePage() {
        this.s3ConfigConfigurationPropertiesTable = $(byId(
                "s3ConfigConfigurationPropertiesTable"));
        this.s3ConfigValueTable = $(byId("s3ConfigValueTable"));
    }

    /**
     * S3設定値を表示しているテーブルを返却する。
     * @return SelenideElement S3設定値テーブル
     */
    public SelenideElement getS3ConfigConfigurationPropertiesTable() {
        return s3ConfigConfigurationPropertiesTable;
    }

    /**
     * S3設定値を表示しているテーブルを返却する。
     * @return SelenideElement S3設定値テーブル
     */
    public SelenideElement getS3ConfigValueTable() {
        return s3ConfigValueTable;
    }
}
