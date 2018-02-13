# ATSC 非同期処理の実装 AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
2. Simple Queue Service(SQS)
3. Simple Notification Service(SNS)
4. CloudWatch

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

### Simple Notification Service (SNS)
1. AWSコンソールにログインし、Simple Notification Service(SNS)サービス画面を開く。
2. 「トピック」を選択し、新しいトピックを作成する。

|設定項目| 値 |
|------|----|
|トピック名|TEST_CWALM|

### Simple Queue Service (SQS)
1. AWSコンソールにログインし、Simple Queue Service(SQS)サービス画面を開く。
2. アラーム通知が送信されるキューを以下の通り作成する。(「キューの作成」参照)
3. 作成したキューを選択した状態で「キュー操作」を押下し、既に作成しているSNSトピックのサブスクライブを行う。（「SNSトピックのサブスクライブ」参照）
   SQSからサブスクライブすることにより、SNSのサブスクライブとSNSからSQSへのアクセス許可が自動で設定される。

#### キューの作成

|設定項目| 値 |
|------|----|
|キュー名| TESTAutoScaleNotification |
|デフォルトの可視性タイムアウト| 30[秒\] |
|メッセージ保持期間 |4[日\] |
|最大メッセージサイズ|256[KB\] |
|配信遅延|0[秒\] |
|メッセージ受信待機時間|20[秒\] |
|デッドレターキュー設定(再処理ポリシーの使用)|未チェック|
|デッドレターキュー設定(デッドレターキュー)|disable状態|
|最大受信数|disable状態|

#### SNSトピックのサブスクライブ

|設定項目| 値 |
|------|----|
|トピックリージョン|アジアパシフィック(東京)|
|トピックの選択|TEST_CWALM|
|トピックARN|(トピックから生成されたARN、自動で設定される)|

### CloudWatch

1. 下準備として、CloudWatchが監視するメトリクスを先に送付する必要がある。
   Mavenによる local, ciプロファイルで試験を空回しすることで、以下のメトリクスがCloudWatchに送信されることを確認する。（「メトリクス情報」を参照）
   この時点ではオートスケーリングのトリガを模したアラームがSQSに届かないため、3分のタイムアウトをもって試験に失敗する。
2. AWSコンソールにログインし、CloudWatchサービス画面から下記「メトリクス情報」にあるメトリクスが送信されていることを確認する。
3. このメトリクスを元にアラームを作成する。（「CludWatchアラームの作成」参照）
4. AWSコンソール上から試験実施前はOK状態、試験実施によりアラームに遷移することを確認する。

#### メトリクス情報

|メトリクス情報|値｜
|------|----|
|カスタム名前空間|local あるいは ci|
|AutoScalingGroupName|atscGroup|
|instanceId|localhost|
|メトリクス名|HeapMemory.Max|

#### CloudWatchアラームの作成

|設定項目| 値 |
|------|----|
|アラームのしきい値(名前)|TestAlarmNotification あるいは TestAlarmNotification-ci|
|次の時|HeapMemory.Max > 0|
|期間｜1中1データポイント|
|間隔|1分間|
|統計|スタンダード、平均|
|追加設定(欠落データの処理方法)|適正(しきい値を超えていない)|
|通知：アクション(アラームが次の時)|状態：警告|
|通知：アクション(通知の送信先)|TEST_CWALM|