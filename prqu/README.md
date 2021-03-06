# PRQU 非同期実行（優先順位の設定） AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. ElastiCache(Redis)
1. SQS

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

1. AWS コンソール(Web)にログイン
2. ElastiCache 画面を開く
3. 「作成」 を押下し、クラスターエンジンで「Redis」を選択し、「有効なクラスターモード」にチェックを入れる。
4. 以下の設定項目を設定して、Redisを作成する。
5. application-ci.ymlを編集する。
6. Simple Queue Service(SQS)画面を開く.
7. 「新しいキューの作成」を押下し、以下の設定項目を設定してSQSのキューを作成する。

|設定項目| 値 | 備考
|------|----|----|
| パラメータグループ | `default.redis3.2.cluster.on` | - |
| シャード数 | 3 | - |
| シャードあたりのレプリカ | 0 | - |

### application-ci.yml

|設定項目| 値 | 備考
|------|----|----|
| `spring.redis.cluster.nodes[0]` | `xxxx.cache.amazonaws.com:6379`| 作成したRedisのエンドポイントに合わせて変更する。 |

### SQS

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | DL_FT_PRIORITY_QUEUE_HIGH | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックしない。 | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | FT_PRIORITY_QUEUE_HIGH | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックする。 | - |
| デッドレターキュー | DL_FT_PRIORITY_QUEUE_HIGH | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | DL_FT_PRIORITY_QUEUE_LOW | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックしない。 | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | FT_PRIORITY_QUEUE_LOW | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックする。 | - |
| デッドレターキュー | DL_FT_PRIORITY_QUEUE_LOW | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | DL_FT_PRIORITY_QUEUE_HIGH_CI | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックしない。 | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | FT_PRIORITY_QUEUE_HIGH_CI | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックする。 | - |
| デッドレターキュー | DL_FT_PRIORITY_QUEUE_HIGH_CI | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | DL_FT_PRIORITY_QUEUE_LOW_CI | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックしない。 | - |

|設定項目| 値 | 備考
|------|----|----|
| キュー名 | FT_PRIORITY_QUEUE_LOW_CI | - |
| リージョン | アジアパシフィック(東京) | - |
| 再処理ポリシー | チェックする。 | - |
| デッドレターキュー | DL_FT_PRIORITY_QUEUE_LOW_CI | - |
