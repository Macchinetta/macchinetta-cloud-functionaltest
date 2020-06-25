
# EVEM ヘルスチェック(evem-config) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. Simple Strage Service(S3)

## この機能試験が使用する他サービス
1. Git(リモートリポジトリが必要)

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順
1. S3バケットを作成する
1. S3バケット直下に`evem`フォルダ配置する
1. `evem`フォルダに、`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`を配置する
1. Gitのリモートリポジトリを作成する
1. Gitのリモートリポジトリの`smpl/src/main/resources`に`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`を配置する

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
  url: jdbc:postgresql://func-master-12x.xxxxxxxxxxxx.ap-northeast-1.rds.amazonaws.com:5432/functionaltest
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

- `macchinetta-cloud-functionaltest/smpl` プロジェクトの`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`ファイルと同じ内容なので割愛

### evem-configプロジェクト内の`application-s3.yml`

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.server.s3.uri` | `s3://xxxx` | 作成したS3のバケット名に合わせて変更する。 |
| `spring.cloud.config.server.s3.searchPaths` | `evem` | `applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`ファイルを配置したS3のフォルダ名を設定する。 |

### evem-configプロジェクト内の`application-gitlocal.yml`

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.server.git.uri` | `https://xxxx/yyyy.git`| 作成したGitのリモートリポジトリのURLを設定する |
| `spring.cloud.config.server.git.searchPaths` | `smpl/src/main/resources` | 作成したGitのリポジトリで、`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`を配置したパスを設定する。 |
| `spring.cloud.config.server.git.username` | `xxxx` | Gitのリモートリポジトリのユーザー名に合わせて変更する。 |
| `spring.cloud.config.server.git.password` | `xxxx` | Gitのリモートリポジトリのパスワードに合わせて変更する。 |

### evem-configプロジェクト内の`application-gitci.yml`

|設定項目| 値 | 備考
|------|----|----|
| `spring.cloud.config.server.git.uri` | `https://xxxx/yyyy.git`| 作成したGitのリモートリポジトリのURLを設定する。(CI環境でも`application-gitlocal.yml`で指定した、GitのリモートリポジトリのURLで通信できるなら、`application-gitlocal.yml`のGitのリモートリポジトリのURLと同じで良い) |
| `spring.cloud.config.server.git.searchPaths` | `smpl/src/main/resources` | 作成したGitのリポジトリで、`applocation.yml`、`applocation-default.yml`、`applocation-ci.yml`を配置したパスを設定する。 |
| `spring.cloud.config.server.git.username` | `xxxx` | Gitのリモートリポジトリのユーザー名に合わせて変更する。 |
| `spring.cloud.config.server.git.password` | `xxxx` | Gitのリモートリポジトリのパスワードに合わせて変更する。 |
