# PRDI プライベートダウンロード AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. Simple Storage Service(S3)

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

#### S3

1. AWS コンソール(Web)にログイン
2. S3 画面を開く
3. 試験で使用するS3バケットを作成する。(以降、xxxxと表記)
4. 作成したS3バケットのアクセス権限から、以下設定でCORSの設定を行う。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
<CORSRule>
    <AllowedOrigin>*</AllowedOrigin>
    <AllowedMethod>GET</AllowedMethod>
    <MaxAgeSeconds>3000</MaxAgeSeconds>
    <AllowedHeader>Authorization</AllowedHeader>
</CORSRule>
</CORSConfiguration>
```

### application.yml

1. 上記で作成したバケット名を登録する。

```yml
functionaltest:
    download:
      bucketName: xxxx
```

## ローカル端末時間修正
AWS S3の時刻と同期が取れていない場合、正しく試験が実施できないため試験前に以下の確認を行う。
1. Tomcatからprdiを立ち上げ、F12開発者ツールからネットワークのPOST,GETを監視する。
2. こちらのアップロードファイルの送信時刻、AWS S3からのファイル取得時刻の二つを比較し、5秒以上のずれがあった場合はローカル側の時刻を修正してS3側となるべく時間が同一になるように同期を取る。
