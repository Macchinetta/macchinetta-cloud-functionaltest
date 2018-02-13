# ATSC 非同期処理の実装 AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
2. Simple Queue Service(SQS)
3. Simple Notification Service(SNS)
4. Simple Email Service(SES)

## リージョン
1. バージニア北部 : us-east-1

## 簡易構築手順

### Simple Notification Service (SNS)
1. AWSコンソールにログインし、Simple Notification Service(SNS)サービス画面を開く。
2. 「トピック」を選択し、新しいトピックを3つ作成する。

|設定項目| 値 |用途|
|------|----|---|
|トピック名|SESDLVRS|メール正常配信通知|
|トピック名|SESBOUNCES|バウンスメール通知|
|トピック名|SESCMPLNTS|苦情通知|

### Simple Queue Service (SQS)
1. AWSコンソールにログインし、Simple Queue Service(SQS)サービス画面を開く。
2. キューを以下の通り作成する。(「キューの作成」参照)
3. 作成したキューを選択した状態で「キュー操作」を押下し、既に作成しているSNSトピックのサブスクライブを行う。（「SNSトピックのサブスクライブ」参照）
   SQSからサブスクライブすることにより、SNSのサブスクライブとSNSからSQSへのアクセス許可が自動で設定される。

#### キューの作成

|設定項目| 値 |
|------|----|
|キュー名| TEST_SQS_DELIVERIES |
|デフォルトの可視性タイムアウト| 30[秒\] |
|メッセージ保持期間 |4[日\] |
|最大メッセージサイズ|256[KB\] |
|配信遅延|0[秒\] |
|メッセージ受信待機時間|20[秒\] |
|デッドレターキュー設定(再処理ポリシーの使用)|未チェック|
|デッドレターキュー設定(デッドレターキュー)|disable状態|
|最大受信数|disable状態|

#### SNSトピックのサブスクライブ 正常配信用

|設定項目| 値 |
|------|----|
|トピックリージョン|米国東部(バージニア北部)|
|トピックの選択|SESDLVRS|
|トピックARN|(トピックから生成されたARN、自動で設定される)|

#### SNSトピックのサブスクライブ バウンスメール用

|設定項目| 値 |
|------|----|
|トピックリージョン|米国東部(バージニア北部)|
|トピックの選択|SESBOUNCES|
|トピックARN|(トピックから生成されたARN、自動で設定される)|

### Simple Email Service(SES)

1. 下準備として、SESのサンドボックス内のシミュレーターで利用可能なEメールアドレスを登録する。
2. AWSコンソールにログインし、Email Addresses メニューを開き、Verify a New Email Address から送信元メールアドレスを登録する。
3. AWSから確認メールが届くため、内容に問題がないことを確認して認証・登録する。
4. AWSコンソールで新規登録されたメールアドレスのStatusがverifiedであることを確認する。
5. 新規登録されたメールアドレスのリンクから、Notificationsを選択してEmail送信シミュレーターからの通知設定を行う。

|SNS Topic Configuration | 設定値 | include original headers |
|------|----|--------------------------|
|Bounces|SESBOUNSES|チェックする|
|Complaints|SESCMPLNTS|チェックする|
|Deliveries|SESDLVRS|チェックする|

|設定項目| 値 |
|------|----|
|Email Feedback Forwarding|Disabled|

### テストアプリケーションの設定

1. 上述で新規登録したEメールアドレスをアプリケーション内に設定する。

#### mlsn/src/main/resources/META-INF/spring/functionaltest-domain.xml

```xml
<bean id="templateMessage" class="org.springframework.mail.SimpleMailMessage">
   <!-- ここに新規に登録した送信元メールアドレスを設定する。-->
   <property name="from" value="xxxx@foo.bar.com"/>
   <property name="subject" value="testmail"/>
</bean>
```

#### mlsn/src/main/java/jp/co/ntt/cloud/functionaltest/app/mlsn/MailSendController.java

```java
// MIME形式による送信者表示名、メールアドレスを設定する。
notification = sesMailSender.registerMime("Xxxx Xxxx <xxxx@foo.bar.com>",
    mailForm.getTo(),
    "UTF-8",
    "MIME Mail test",
    mailForm.getBody());
```
