
# EVEM ヘルスチェック(evem-failfast-true-boot-fail) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順
1. `application.yml`の`spring.cloud.config.uri`をConfig Serverに繋がらないように設定する。

### application.yml

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.uri` | `http://localhost` | Config Serverに繋がらないように設定する。 |
