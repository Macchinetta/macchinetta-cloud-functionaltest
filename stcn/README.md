# STCN 静的コンテンツの配信 AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. CloudFront
2. Simple Strage Service(S3)

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順
1. Amazon S3のバケットとAmazon Cloud Front のウェブディストリビューションを作成する。
1. `macchinetta-cloud-functionaltest\stcn\src\test\resources\testdata\image\ochiboHiroi.jpg`をコピーしてS3の`/resources/image/`配下にファイル名`ochiboHiroi.jpg`として配置する。
1. CloudFrontを通してS3に配置した画像を配信できるようにする。
   - 詳細手順は以下のドキュメントを参照し、ステップ4まで実施する。
   -  http://docs.aws.amazon.com/ja_jp/AmazonCloudFront/latest/DeveloperGuide/GettingStarted.html


### application-local.yml

|設定項目| 値 | 備考
|------|----|----|
| `content.url` | https://xxxxxxx.cloudfront.net |  CloudFront が割り当てたドメイン名に合わせて変更する。 |

### application-ci.yml

|設定項目| 値 | 備考
|------|----|----|
| `content.url` | https://xxxxxxx.cloudfront.net |  CloudFront が割り当てたドメイン名に合わせて変更する。 |
