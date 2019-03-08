
# HLCH ヘルスチェック(hlch-dynamo-up) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. DynamoDB

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

### DynamoDB

1. AWS コンソール(Web)にログイン
2. DynamoDB の 画面を開く
3. 「テーブルの作成」 を押下し、テーブルを1つ作成する。


### application.yml

|設定項目| 値 | 備考
|------|----|----|
| `amazon.dynamodb.endpoint` | `https://xxxx.amazonaws.com` | 作成したDynamoDBのエンドポイントに合わせて変更する。 |
