# PRCDのローカルPC上での起動手順書
## ローカル環境の事前準備
### hostsの設定
 -`C:\Windows\System32\drivers\etc\hosts`に以下の設定を追加する。
  - `127.0.0.1 xxxxxx.net`



### TomcatのSSL設定（WTP）※pom.xmlからcargo起動の場合は設定済みのため不要
  - `${workspace_loc}/Servers/[使用しているTomcat]/server.xml`

         <Connector SSLEnabled="true" clientAuth="false" keystoreFile="${user.home}/.keystore"
         keystorePass="aaaaaa" maxThreads="200" port="8082" protocol="org.apache.coyote.http11.Http11NioProtocol" scheme="https" secure="true" sslProtocol="TLS"/>

  - `/prcd/file/.keystore`を`${user.home}`に配置する。


### private-keyの配置
  - `s3://functionaltest.private.cache.distribution/private-key.der`を`${user.home}`に配置する。
     本手順で用意していない新たなCloudFrontを利用して本機能の試験（CDNプライベート配信）を実施したい場合は、
     信頼された署名者の CloudFront キーペアを作成する必要がある。
     http://docs.aws.amazon.com/ja_jp/AmazonCloudFront/latest/DeveloperGuide/private-content-trusted-signers.html#private-content-creating-cloudfront-key-pairs

### 自己署名証明書の例外設定
`xxxxxx.net` との通信には自己署名証明書を使用している。
FireFoxのユーザプロパティに証明書の例外設定を行い、テスト実行時に読み込みを行う。

 -  「ファイル名を指定して実行」にて `firefox.exe -P` を入力しFireFoxを起動
 - 「新しいプロファイルを作成」 を選択し、新規プロファイルを作成（名称は任意）
 - prcdの`pom.xml`が格納されているフォルダに移動
 - コマンドプロンプトにて`mvn clean package cargo:run` を実行しTomcat起動
 - FireFoxで`https://xxxxxx.net:8082/prcd` にアクセス
 - FireFoxの「安全ではない接続」画面で「エラー内容」ボタンを選択し、「例外を追加」ボタンを選択する。セキュリティ例外の追加ダイアログにて「セキュリティ例外を承認」を選択する。
 - `C:\Users\[ユーザ名]\AppData\Roaming\Mozilla\Firefox\Profiles` 配下に`XXXXXXXX.[プロファイル名]` のユーザプロファイルディレクトリが作成されるためパスをコピー
 - `/prcd/src/test/resources/META-INF/spring/selenide.properties` の`userProfilePath` にユーザプロファイルのパスを設定する。（Windowsの場合、パス記号`\` は `\\` とすること）

## AWSの事前準備
### S3にコンテンツの配置
 - `/prcd/src/test/resources/testdata`の配下にテスト用のファイルがあるのでS3の以下に配置
   - `functionaltest.private.cache.distribution/prcd/paid/2.png`
   - `functionaltest.private.cache.distribution/prcd/reject/1.png`


作成したバケットはテストの為にCORSの設定を行う。
※以下はサンプル

    <?xml version="1.0" encoding="UTF-8"?>
    <CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
    <CORSRule>
        <AllowedOrigin>https://xxxxxx.net</AllowedOrigin>
        <AllowedOrigin>http://xxxxxx.net</AllowedOrigin>
        <AllowedOrigin>http://xxxxxx.net:8082</AllowedOrigin>
        <AllowedOrigin>https://xxxxxx.net:8082</AllowedOrigin>
        <AllowedMethod>GET</AllowedMethod>
        <AllowedMethod>HEAD</AllowedMethod>
        <MaxAgeSeconds>3000</MaxAgeSeconds>
        <AllowedHeader>Content-*</AllowedHeader>
        <AllowedHeader>Host</AllowedHeader>
        <AllowedHeader>*</AllowedHeader>
    </CORSRule>
    </CORSConfiguration>

### CloudFrontの設定
今回は`xxxxxx.net`をドメイン名としてクラウドフロント側のサブドメイン`www.xxxxxx.net`でアクセスするための設定を行っている。ドメインの変更は`/prcd/src/main/resources/application.yml`に設定することで変更可能

    functionaltest:
      cf:
        signature:
          protocol: https
          secure: true
          domain: xxxxxx.net
          distributionDomain: www.xxxxxx.net

#### CloudFront側の設定は以下のAWSのドキュメントを参考に設定する

##### CloudFront を使用してプライベートコンテンツを供給する
http://docs.aws.amazon.com/ja_jp/AmazonCloudFront/latest/DeveloperGuide/PrivateContent.html
##### CloudFront で HTTPS を使用する
http://docs.aws.amazon.com/ja_jp/AmazonCloudFront/latest/DeveloperGuide/using-https.html
##### Amazon S3 での CloudFront の使用
http://docs.aws.amazon.com/ja_jp/AmazonCloudFront/latest/DeveloperGuide/MigrateS3ToCloudFront.html
##### 代替ドメイン名と HTTPS の使用
http://docs.aws.amazon.com/ja_jp/AmazonCloudFront/latest/DeveloperGuide/using-https-alternate-domain-names.html
