
# EVEM ヘルスチェック(evem-config-enabled-false) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

### bootstrap.yml

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.uri` | `http://localhost:8888` | `macchinetta-cloud-functionaltest/evem-config` のConfig ServerのURLに合わせて変更する。 |
