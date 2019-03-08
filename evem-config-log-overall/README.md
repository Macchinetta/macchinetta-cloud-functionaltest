
# EVEM ヘルスチェック(evem-config-log-overall) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. Simple Strage Service(S3)

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順
1. S3バケットを作成する
1. S3バケット直下に`evem`フォルダ配置する
1. `evem`フォルダに、`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`を配置する

### S3の`evem`フォルダ直下に`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`ファイルの内容

- `applocation-default.yml`
```
# 環境依存しないフレームワークの設定値

spring:
  application:
    name: evem

cloud:
  aws:
    stack:
      auto: false
```

- `applocation-default.yml`
```
# for development environment on local PC
cloud:
  aws:
    # AWS common
    region:
      static: ap-northeast-1
      auto: false
# 認証情報の取得抑制
    credentials:
      instanceProfile: false

# RDB(ローカルPC上のPostgreSQLを使用)
database:
  url: jdbc:postgresql://localhost:5432/functionaltest
  username: postgres
  password: postgres
  driverClassName: org.postgresql.Driver

# connection pool for RDB
cp:
  maxActive: 96
  maxIdle: 16
  minIdle: 16
  initialSize: 16
  maxWait: 60000
  validationQuery: SELECT 1
  testOnConnect: true
  testWhileIdle: true
  timeBetweenEvictionRunsMillis: 120000
  removeAbandoned: true
  removeAbandonedTimeout: 60

# upload directories
upload:
  bucketname: functionaltest.external.properties.config.repo
  temproryDirectory: tmp/
  saveDirectory: save/

# Logging
logging:
  path: /var/log/applogs
```


- `applocation-ci.yml`
```
# for CI environment on EC2
    # AWS common
cloud:
  aws:
    region:
    auto: true

# RDB(RDSを使用)
database:
  url: jdbc:postgresql://ci-functionaltest.xxxxxxxxxxxx.ap-northeast-1.rds.amazonaws.com:5432/functionaltest
  username: postgres
  password: postgres
  driverClassName: org.postgresql.Driver

# connection pool for RDB
cp:
  maxActive: 96
  maxIdle: 16
  minIdle: 16
  initialSize: 16
  maxWait: 60000
  validationQuery: SELECT 1
  testOnConnect: true
  testWhileIdle: true
  timeBetweenEvictionRunsMillis: 120000
  removeAbandoned: true
  removeAbandonedTimeout: 60

  # upload directories
upload:
  bucketname: functionaltest.external.properties.config.repo
  temproryDirectory: tmp/
  saveDirectory: save/

# Logging
logging:
  path: /var/log/applogs
```

### Gitのリモートリポジトリの`smpl/src/main/resources`に配置した、`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`ファイルの内容

- macchinetta-cloud-functionaltest\smpl プロジェクトの`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`ファイルと同じ内容なので割愛

### evem-configプロジェクト内の`application.yml`

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.server.s3.uri` | `s3://xxxx` | 作成したS3のバケット名に合わせて変更する。 |
| `spring.cloud.config.server.s3.searchPaths` | `evem` | `applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`ファイルを配置したS3のフォルダ名を設定する。 |
