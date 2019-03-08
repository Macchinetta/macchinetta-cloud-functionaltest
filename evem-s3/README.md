
# EVEM ヘルスチェック(evem-s3) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. Simple Strage Service(S3)

## この機能試験が使用する他サービス
1. `macchinetta-cloud-functionaltest/evem-config` プロジェクトで作成したConfig Server

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順
1. `macchinetta-cloud-functionaltest/evem-config` でS3からプロパティファイルを取得できるように設定しておく。(`macchinetta-cloud-functionaltest/evem-config/README.md`も参照すること)

### bootstrap.yml

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.uri` | `http://localhost:8888` | `macchinetta-cloud-functionaltest/evem-config` のConfig ServerのURLに合わせて変更する。 |
