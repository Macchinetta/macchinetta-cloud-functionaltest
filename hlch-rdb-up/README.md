
# HLCH ヘルスチェック(hlch-rdb-up) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. DynamoDB
1. RDS

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

### application.yml

|設定項目| 値 | 備考
|------|----|----|
| `amazon.dynamodb.endpoint` | `https://xxxx.amazonaws.com` | 作成したDynamoDBのエンドポイントに合わせて変更する。 |
