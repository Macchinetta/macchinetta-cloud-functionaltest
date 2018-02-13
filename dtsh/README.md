# DTSH データベースシャーディング AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. DynamoDB

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

1. PostgreSQL(user:postgres, username:postgres)にDBを作成する
    - `CREATE DATABASE functionaltest WITH ENCODING = 'UTF8';`
    - `CREATE DATABASE functionaltest_shard1 WITH ENCODING = 'UTF8';`
    - `CREATE DATABASE functionaltest_shard2 WITH ENCODING = 'UTF8';`
1. DynamoDBにアクセスできるようにする。

### application-default.yml

|設定項目| 値 | 備考
|------|----|----|
| `database.data-sources[0].url` | `jdbc:postgresql://xxxx/functionaltest`| 作成したRDBのエンドポイントに合わせて変更する。 |
| `database.data-sources[1].url` | `jdbc:postgresql://xxxx/functionaltest_shard1`| 作成したRDBのエンドポイントに合わせて変更する。 |
| `database.data-sources[2].url` | `jdbc:postgresql://xxxx/functionaltest_shard2`| 作成したRDBのエンドポイントに合わせて変更する。 |

### application-ci.yml

|設定項目| 値 | 備考
|------|----|----|
| `database.data-sources[0].url` | `jdbc:postgresql://xxxx/functionaltest`| 作成したRDBのエンドポイントに合わせて変更する。 |
| `database.data-sources[1].url` | `jdbc:postgresql://xxxx/functionaltest_shard1`| 作成したRDBのエンドポイントに合わせて変更する。 |
| `database.data-sources[2].url` | `jdbc:postgresql://xxxx/functionaltest_shard2`| 作成したRDBのエンドポイントに合わせて変更する。 |

### pom.xml

|設定項目| 値 | 備考
|------|----|----|
| DynamoDBのテーブル名 | `<dynamo.db.tablename>xxxx</dynamo.db.tablename>` | DynamoDBのテーブル名を設定する。Maven のAnt taskでテーブルを作成しているので、事前にテーブルを作成する必要はない。 |
| 初期化対象のデフォルトDBのURL |  `<db.url.default>jdbc:postgresql://xxxx/functionaltest</db.url.default>` | 作成したRDBのエンドポイントに合わせて変更する。 |
| 初期化対象のシャード1のURL |  `<db.url.shard1>jdbc:postgresql://xxxx/functionaltest_shard1</db.url.shard1>` | 作成したRDBのエンドポイントに合わせて変更する。 |
| 初期化対象のシャード2のURL |  `<db.url.shard2>jdbc:postgresql://xxxx/functionaltest_shard2</db.url.shard2>` | 作成したRDBのエンドポイントに合わせて変更する。 |
| 初期化対象のDBのユーザー名 | `<db.username>xxxx</db.username>` | 作成したRDBのユーザー名に合わせて変更する。|
| 初期化対象のDBのパスワード | `<db.password>xxxx</db.password>` | 作成したRDBのパスワードに合わせて変更する。|

### jp.co.ntt.cloud.functionaltest.domain.common.shard.model.ShardingAccount.java
|設定項目| 値 | 備考
|------|----|----|
| DynamoDBのテーブル名 | `@DynamoDBTable(tableName = "xxxx")` | `pom.xml`の`<dynamo.db.tablename>`で設定したDynamoDBのテーブル名と同じ値に設定する。 |
