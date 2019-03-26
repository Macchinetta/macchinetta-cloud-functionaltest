# Macchinetta Cloud Functional Test

## 雛形の利用方法について
macchinetta-cloud-functionaltestには、雛形プロジェクト「smpl」を予め作成している。
smplは以下の機能を持つサンプルである。
- 認証
- HelloWorldの画面表示

このsmplをコピーして、各自のプロジェクトをmacchinetta-cloud-functionaltest配下に作成する。

コピー後は、プロジェクト名を各自の名称に変更して、開発を行う。
プロジェクト名を変更する箇所は以下の通り。
- プロジェクト名
- プロジェクト/pom.xmlのartifactId
- macchinetta-cloud-functionaltest/pom.xmlのmoduleに追加
- application.ymlのspring.application.name
- test/resources/META-INF/spring/selenide.propertiesのtarget.contextName＝smpl

## ローカル環境構築手順

**使用ツール**

* [PostgreSQL 10.x](https://www.postgresql.org/download/windows/)
* [Open JDK 8u161](https://openjdk.java.net/install/index.html) (Windowsの場合は[Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html))
* [Open JDK 11.0.2](https://openjdk.java.net/install/index.html) (Windowsの場合は[Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html))
* [Redis 3.x.x](https://github.com/MicrosoftArchive/redis/releases)
* [AWS CLI](https://docs.aws.amazon.com/ja_jp/streams/latest/dev/kinesis-tutorial-cli-installation.html)
* [Tomcat 9.0.10](https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.10/bin/)
* [git 2.x.x](https://git-scm.com/downloads)
* [STS 3.x.x](https://spring.io/tools3) (Eclipseでも可)
* [Maven 3.3.9](https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.3.9/)
* [Firefox ESR 60.3.0](https://ftp.mozilla.org/pub/firefox/releases/60.3.0esr/) ※最新版では動作しない為、注意

### 各使用ツールのダウンロード・インストール

#### PostgreSQL
* PostgreSQL公式ページより規定のバージョンのインストーラをダウンロードする。
* 公式ページおよびアーカイブに規定バージョンの利用OS向けのインストーラが存在しない場合、
同一のメジャーバージョンのインストーラを使用する。
* インストール場所の指定はない。

#### Open JDK および Oracle JDK
* 利用OSに応じて公式ページよりJDKインストーラをダウンロードする。
* Oracle JDKを使用する場合、過去バージョンのJDKのダウンロードにはOracleへのユーザ登録が必要となる。
* インストール場所の指定はない。
* インストール後、以下の手順によりJavaの環境変数を登録する。
  * [コントロールパネル]→[システム]→[システムの詳細設定]→[環境変数]を押下
  * [システム環境変数(S)]の[新規(W)]を押下
  * 以下の設定を入力後、[OK]を押下
    * 変数名 : `JAVA_HOME`
    * 変数値 : (インストールしたJDKのフォルダ)
  * [システム環境変数(S)]の変数名「Path」を選択し、[編集(I)]を押下
  * 以下の設定を入力後、[OK]を押下
    * 変数値 : `%JAVA_HOME%\bin;`(+既存の値)
      * 変数`%JAVA_HOME%`を有効にするため、先頭に追加入力すること。
  * [環境変数]ダイアログおよび[システムのプロパティ]ダイアログで[OK]ボタンを押下して設定を反映させる。

#### Redis
* 公式ページより`Redis-x64-3.x.x.msi`をダウンロードしてインストールする。  
参考：http://qiita.com/kbdx/items/fdd36e895acf2532f0bd  
  - Redisの起動確認  
    下記コマンドを入力し、Redisのサービスが起動していることを確認する。  
    - `sc query redis`  
  - Redisの起動  
    Redisが起動していない場合、下記コマンドを入力し、Redisを起動する。  
    - `net start redis`  
* Redisの多重起動用のサービスを作成する。
  * コマンドプロンプトを起動し、Redisのインストールフォルダまで移動する。
  * 以下のコマンドを実行する。
  ~~~
  redis-server.exe --service-install --service-name "redis port6380"  --port 6380
  ~~~
  * コントロールパネルの[管理ツール]→[サービス]から、「redis port6380」が追加されていることを確認する。
  * 「redis port6380」を右クリックからプロパティを開き、[スタートアップの種類]を[自動]に更新し、[OK]を押下する。
  * 「redis port6380」を右クリックし、[開始]を押下する。

#### AWS CLI(およびローカル環境でのCredentialsの管理)
ローカル環境でのAWS認証情報の管理は、CLIにて行う。
利用端末で下記手順を実施すること。

* [インストーラー](https://aws.amazon.com/jp/cli/)をDLし、インストール
* ``aws configure`` でアクセスキーなどを設定

```console
$ aws configure
AWS Access Key ID [None]: <アクセスキーJD>
AWS Secret Access Key [None]:<アクセスキー>
Default region name [None]:東京の場合はap-northeast-1
Default output format [None]:json
```

#### Tomcat
* Mavenによる試験実施のみである場合はスキップしてもよい。
* 試験機能の画面を実際に操作したい場合は、公式ページおよびアーカイブから該当バージョンのインストーラをダウンロードする。
* インストール場所の指定はない。

#### git
* 公式ページおよびアーカイブから該当バージョンのインストーラをダウンロードする。
* インストール場所の指定はない。

#### STS 3 および Eclipse
* Mavenによる試験実施のみである場合はスキップしてもよい。
* 試験機能の画面を実際に操作したい場合は、公式ページからインストーラをダウンロードする。
* インストール場所の指定はない。

#### Maven
* 公式ページからのインストーラをダウンロードする。
* インストール場所の指定はない。

#### Firefox
* 公式ページおよびアーカイブから利用OS向けの該当バージョンのインストーラをダウンロードする。
* インストール場所の指定はない。
* インストール後Firefoxを起動し、オプションを開く。
* [詳細画面]から、[更新]タブを開き、[Firefoxの更新]の設定を[更新の確認は行わない]に設定する。


## テスト実施方法(ローカル環境)

### [Step 1] DB作成(PostgreSQL)

ローカル環境にPostgreSQLのDBを作成する。
* DB名:functionaltest
  * user:postgres
  * pass:postgres
* DB名:functionaltest-shard1
  * user:postgres
  * pass:postgres
* DB名:functionaltest-shard2
  * user:postgres
  * pass:postgres

### [Step 2] リポジトリクローン
`macchinetta-cloud/macchinetta-cloud-functionaltest`リポジトリをローカルにクローンする。

```console
$ git clone https://{remote host url}/macchinetta-cloud/macchinetta-cloud-functionaltest.git
```

### [Step 3] 機能ごとの詳細設定・資材修正
* 一部の機能IDの試験ではローカル環境ごとの追加の設定および初期値の修正などが発生するため、資材の修正を必要とする。
* 詳細な修正内容や対象資材については各機能IDごとのプロジェクトのREADME.mdを参照。

### [Step 4] ビルド&テスト実施
親プロジェクトのmacchinetta-cloud-functionaltestに対してmvn installを実行すれば、デプロイおよびテスト実施まで行われる。

```console
$ cd {your repository directory}
$ git checkout {target branch}
$ mvn clean install
```

## テスト実施方法(CI環境)

### [Step 1] 作業ブランチをPushする
(プルリクエストした場合はこのタイミングで実行される)

### [Step 2] Jenkinsでブランチ指定して実行

[Jenkins](https://xxxxxx.ap-northeast-1.elb.amazonaws.com/jenkins/job/macchinetta-cloud-functionaltest/build?delay=0sec)
BRANCHにブランチ名を入れてビルドボタンを押下すればよい。

左ペインにあるビルド番号のリンク先でテスト結果を確認できる。
※「コンソール確認」も併せて確認すること。

## プロジェクト一覧
テスト内容に応じてアルファベット4文字の機能IDを付与し、プロジェクト名としている。
プロジェクトの一覧は以下のとおり。

|機能ID（プロジェクト名） | テスト内容 |
|------|----|
| CWAP | クラウド版開発プロジェクトの作成 |
| UPFM | アップロードファイル管理 |
| STCN | 静的コンテンツの配信 |
| ASPR | 非同期処理の実装 |
| PRQU | 非同期実行（優先順位の設定） |
| EVEM | 環境依存値の外部管理 |
| HLCH | ヘルスチェック |
| ISUE | インターネットストレージ内ファイルの効率的な検索 |
| DTSH | データベースシャーディング |
| CCAB | キャッシュの抽象化（Cache Abstraction） |
| CAAP | AWS版開発プロジェクトの作成 |
| CAAB | キャッシュ抽象化 |
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
