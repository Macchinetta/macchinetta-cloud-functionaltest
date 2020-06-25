
# EVEM ヘルスチェック(evem-dir-traversal) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS

## この機能試験が使用する他サービス
1. Git(リモートリポジトリが必要)
1. `macchinetta-cloud-functionaltest/evem-config` プロジェクトで作成したConfig Server

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順
1. `macchinetta-cloud-functionaltest/evem-config` でGitからプロパティファイルを取得できるように設定しておく。(`macchinetta-cloud-functionaltest/evem-config/README.md`も参照すること)

### bootstrap.yml

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.uri` | `http://localhost:8888` | `macchinetta-cloud-functionaltest/evem-config` のConfig ServerのURLに合わせて変更する。 |

## ローカル試験実施時の修正箇所
`pom.xml`の153行目にインストールしたMavenの`mvn.cmd`ファイルパスを記載するため、ローカル環境に応じて記載を修正する。

異常系の試験を行う場合は、154,168行目の `dirsuccess` を `dirfalure` に変更する。
