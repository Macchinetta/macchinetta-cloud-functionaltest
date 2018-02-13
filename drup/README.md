# DRUP ダイレクトアップロード機能 AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. S3

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

1. AWS コンソール(Web)にログイン
2. S3 画面を開く
3. 「+ バケットを作成する」ボタンを押下し、任意のバケット名を入力して「作成」を行う。
4. application.yml、selenide.propertiesを編集する。

|設定項目| 値 | 備考
|------|----|----|
| バケット名 | 任意(ピリオド'.'を含まないDNS準拠の文字列) | - |
| リージョン | アジアパシフィック(東京) | - |
| 既存のバケットから設定をコピー | (空欄) | - |

### application.yml

|設定項目| 値 | 備考
|------|----|----|
| `functionaltest.upload.bucketName` | 任意(ピリオド'.'を含まないDNS準拠の文字列) | 手順2.で設定したバケット名 |

### selenide.properties

|設定項目| 値 | 備考
|------|----|----|
| `bucketName` | 任意(ピリオド'.'を含まないDNS準拠の文字列) | 手順2.で設定したバケット名 |