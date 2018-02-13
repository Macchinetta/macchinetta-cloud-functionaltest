実行する前にDBを作成する。

1.PostgreSQL(user:postres, username:postgres)にDBを作成する
* CREATE DATABASE functionaltest WITH ENCODING = 'UTF8';
* CREATE DATABASE functionaltest_shard1 WITH ENCODING = 'UTF8';
* CREATE DATABASE functionaltest_shard2 WITH ENCODING = 'UTF8';

2.DynamoDBに以下のテーブルを作成すること
* functionaltestShardAccount