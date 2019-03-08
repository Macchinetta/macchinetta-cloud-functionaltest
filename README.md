# Macchinetta Cloud Functional Test

## 雛形の利用方法について
macchinetta-cloud-functionaltestには、雛形プロジェクト「smpl」を予め作成してあります。
smplは以下の機能を持つサンプルです。
- 認証
- HelloWorldの画面表示

このsmplをコピーして、各自のプロジェクトをmacchinetta-cloud-functionaltest配下に作成して下さい。

コピー後は、プロジェクト名を各自の名称に変更して、開発を行って下さい。
プロジェクト名を変更する箇所は以下の通り。
- プロジェクト名
- プロジェクト/pom.xmlのartifactId
- macchinetta-cloud-functionaltest/pom.xmlのmoduleに追加
- application.ymlのspring.application.name
- test/resources/META-INF/spring/selenide.propertiesのtarget.contextName＝smpl

## テスト実施方法(ローカル環境)

**使用ツール:**

* [Maven](https://maven.apache.org/download.cgi)
* Firefox ESR 52.9.0 ※最新版では動作しない為、注意

### [Step 1] DB作成(PostgreSQL)

ローカル環境にPostgreSQLのDBを作成する。
DB名:functionaltest
user:postgres
pass:postgres

### [Step 2] リポジトリクローン
`macchinetta-cloud/macchinetta-cloud-functionaltest`リポジトリをローカルにクローンする。

```console
$ git clone https://{remote host url}/macchinetta-cloud/macchinetta-cloud-functionaltest.git
```

### [Step 3] ビルド&テスト実施
親プロジェクトのmacchinetta-cloud-functionaltestに対してmvn installを実行すれば、デプロイおよびテスト実施まで行われる。

```console
$ cd {your repository directory}
$ git checkout {target branch}
$ mvn clean install
```

## Credentialsの管理(ローカル環境)
ローカル環境でのAWS認証情報の管理は、CLIにて行う。
利用端末で下記手順を実施すること。

### 初期セットアップ
* [インストーラー](https://aws.amazon.com/jp/cli/)をDLし、インストール
* ``aws configure`` でアクセスキーなどを設定

```console
$ aws configure
AWS Access Key ID [None]: <アクセスキーJD>
AWS Secret Access Key [None]:<アクセスキー>
Default region name [None]:東京の場合はap-northeast-1
Default output format [None]:json
```

## テスト実施方法(CI環境)

### [Step 1] 作業ブランチをPushする
(プルリクエストしてたらこのタイミングで実行されます)

### [Step 2] Jenkinsでブランチ指定して実行

[Jenkins](https://xxxxxx.ap-northeast-1.elb.amazonaws.com/jenkins/job/macchinetta-cloud-functionaltest/build?delay=0sec)
BRANCHにブランチ名を入れてビルドボタンを押下すればOK

左ペインにあるビルド番号のリンク先でテスト結果を確認できる。
※「コンソール確認」も併せて確認のこと

## プロジェクト一覧
テスト内容に応じてアルファベット4文字の機能IDを付与し、プロジェクト名としている。
プロジェクトの一覧は以下のとおり。

|機能ID（プロジェクト名） | テスト内容 |
|------|----|
| CWAP | クラウド版開発プロジェクトの作成 |
| SSMN | セッション外部管理 |
| UPFM | アップロードファイル管理 |
| STCN | 静的コンテンツの配信 |
| ASPR | 非同期処理の実装 |
| PRQU | 非同期実行（優先順位の設定） |
| EVEM | 環境依存値の外部管理 |
| HLCH | ヘルスチェック |
| DTSH | データベースシャーディング |
| CCAB | キャッシュの抽象化（Cache Abstraction） |
| CAAP | AWS版開発プロジェクトの作成 |
| ATSC | オートスケーリングの利用 |
| SSMN | セッション外部管理 |
| DRUP | ダイレクトアップロード |
| PRDI | プライベートダウンロード |
| PRCD | CDNを用いたプライベート配信 |
| MLSN | メール送信 |
| RDRP | データベースリードレプリカ |

### EVEM, HCLH の派生プロジェクトの概要

EVEM, HCLHには派生プロジェクトとして複数存在する。その概要を以下に記す。

#### EVEMの派生プロジェクト概要

|プロジェクト名 | テスト内容 |
|------|----|
| evem-config | Config Server 本体 |
| evem-config-log-overall | logの設定をファイルごとに切り替えができることの確認 |
| evem-config-log-part | logの設定をファイルの一部で行うことができることの確認 |
| evem-failfast-false | `spring.cloud.config.fail-fast: false` に設定しClient AP内のプロパティから値を取得して起動できることの確認 |
| evem-config-enabled-false | Config Serverに接続しない状態で、Client AP内のプロパティから値を取得して起動できることの確認 |
| evem-failfast-true-boot-fail | `spring.cloud.config.fail-fast: true` に設定し動しないことの確認 |
| evem-git | Config Serverをgitからプロパティ値を取得して起動できることの確認 |
| evem-s3 | Config ServerをS3からプロパティ値を取得して起動できることの確認 |


#### HCLHの派生プロジェクト概要

|プロジェクト名 | テスト内容 |
|------|----|
| hlch-dynamo-up | DynamoDBのヘルスチェック結果を取得し、`status`が`UP`になることの確認 |
| hlch-dynamo-down | DynamoDBのエンドポイントを存在しないエンドポイントに設定し、DynamoDBと全体の`status`が`DOWN`になることの確認 |
| hlch-disable | DynamoDBのカスタムヘルスインジケータを実装しているが、プロパティ設定で無効化していることの確認 |
| hlch-rdb-up | DynamoDBのカスタムヘルスインジケータを実装していない、RDBに接続できることの確認 |
| hlch-rdb-down | DynamoDBのカスタムヘルスインジケータを実装していない、RDBに接続できないことの確認 |

