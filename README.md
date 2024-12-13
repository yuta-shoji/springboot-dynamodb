# Architecture for using DynamoDB with Spring Boot

## 継承設計パターン

Spring BootでDynamoDBを扱う上で、どのようなアーキテクチャを組んだら幸せになるか考える
https://github.com/yuta-shoji/springboot-dynamodb/tree/main
1. 継承設計パターン（[mainブランチ](https://github.com/yuta-shoji/springboot-dynamodb/tree/main), [extends-patternブランチ](https://github.com/yuta-shoji/springboot-dynamodb/tree/extends-pattern)）
2. DI設計パターン（[di-patternブランチ](https://github.com/yuta-shoji/springboot-dynamodb/tree/di-pattern)）

このブランチでは、継承設計パターンで実装されています

## 起動方法

### .envの記述
```txt
export DYNAMODB_ENDPOINT=http://localhost:8881
export DYNAMODB_REGION=ap-northeast-1
export DYNAMODB_TABLE_NAME_SUFFIX=local
```

### DynamoDB-Local, DynamoDB-Admin 起動

```bash
docker compose up
```

### Spring Boot起動
```bash
make start
```
起動すると`DynamoDBInitializer`が作用し 、自動的に`main_table_local` `event_table_local`の2テーブルが作成されます。

### DynamoDB Local AdminのGUIにアクセス
http://localhost:8003 簡単なGUIでテーブル操作を行うことができます。
![img.png](image/dynamodb-admin.png)


## 設計指針
「JPAライク」に重きを置いた設計です。
後に説明する`DynamoDBRepository`を、各Repositoryが継承して、抽象化された`DynamoDBRepository`のメソッドを使用してQueryやScan、Saveをするという設計思想です。

![img_1.png](image/architecture1.png)

![img.png](image/architecture2.png)