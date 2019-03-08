# SSMN セッション外部管理(ssmn) AWS環境構築メモ

## この機能試験が使用するAWSサービス
1. RDS
1. ElastiCache(Redis)

## リージョン
1. アジアパシフィック(東京) : ap-northeast-1

## 簡易構築手順

1. AWS コンソール(Web)にログイン
2. ElastiCache 画面を開く
3. 「作成」 を押下し、クラスターエンジンで「Redis」を選択し、「有効なクラスターモード」にチェックを入れる。
4. 以下の設定項目を設定して、Redisを作成する。

|設定項目| 値 | 備考
|------|----|----|
| パラメータグループ | `default.redis3.2.cluster.on` | - |
| シャード数 | 3 | - |
| シャードあたりのレプリカ | 0 | - |

### application-ci.yml

|設定項目| 値 | 備考
|------|----|----|
| `spring.redis.cluster.nodes[0]` | `xxxx`| 作成したRedisのエンドポイントに合わせて変更する。 |

### pom.xml

|設定項目| 値 | 備考
|------|----|----|
| Redisがクラスターモードのときに設定するRedisのエンドポイント | `<redis.cluster.endpoint>xxxx</redis.cluster.endpoint>` | - |
| Redisがクラスターモードでないときに設定するRedisのエンドポイント |  `<redis.host>localhost</redis.host>` | 作成したRedisのエンドポイントに合わせて変更する。 |
| Redisがクラスターモードでないときに設定するRedisのポート番号 |  `<redis.port>6379</redis.port>` | 作成したRedisのポート番号に合わせて変更する。 |
