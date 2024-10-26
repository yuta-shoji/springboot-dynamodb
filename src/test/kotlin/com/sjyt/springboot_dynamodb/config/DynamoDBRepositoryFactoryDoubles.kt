package com.sjyt.springboot_dynamodb.config

import com.sjyt.springboot_dynamodb.config.dynamodb.NoSQLRepositoryFactory
import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import com.sjyt.springboot_dynamodb.repository.doubles.DummyDynamoDBRepository

class DummyDynamoDBRepositoryFactory<Table: TableEntity> : NoSQLRepositoryFactory<Table> {
    override fun build(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        return DummyDynamoDBRepository()
    }
}

class SpyDynamoDBRepositoryFactory<Table: TableEntity> : NoSQLRepositoryFactory<Table> {
    var build_argument_tableClass: Class<Table>? = null
    override fun build(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        build_argument_tableClass = tableClass
        return DummyDynamoDBRepository()
    }
}


class StubDynamoDBRepositoryFactory<Table: TableEntity> : NoSQLRepositoryFactory<Table> {
    var build_returnValue: NoSQLRepository<Table> = DummyDynamoDBRepository()
    override fun build(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        return build_returnValue
    }
}
