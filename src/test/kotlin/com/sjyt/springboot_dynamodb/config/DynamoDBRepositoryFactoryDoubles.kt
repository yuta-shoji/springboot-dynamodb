package com.sjyt.springboot_dynamodb.config

import com.sjyt.springboot_dynamodb.config.dynamodb.NoSQLFactory
import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.NoSQLEnhancedRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import com.sjyt.springboot_dynamodb.repository.doubles.DummyDynamoDBEnhancedRepository
import com.sjyt.springboot_dynamodb.repository.doubles.DummyDynamoDBRepository

class DummyDynamoDBFactory<Table: TableEntity> : NoSQLFactory<Table> {
    override fun buildDynamoDBRepository(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        return DummyDynamoDBRepository()
    }

    override fun buildDynamoDBEnhancedRepository(): NoSQLEnhancedRepository {
        return DummyDynamoDBEnhancedRepository()
    }
}

class SpyDynamoDBFactory<Table: TableEntity> : NoSQLFactory<Table> {
    var build_argument_tableClass: Class<Table>? = null
    override fun buildDynamoDBRepository(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        build_argument_tableClass = tableClass
        return DummyDynamoDBRepository()
    }

    var buildDynamoDBEnhancedRepository_wasCalled = false
    override fun buildDynamoDBEnhancedRepository(): NoSQLEnhancedRepository {
        buildDynamoDBEnhancedRepository_wasCalled = true
        return DummyDynamoDBEnhancedRepository()
    }
}


class StubDynamoDBFactory<Table: TableEntity> : NoSQLFactory<Table> {
    var build_returnValue: NoSQLRepository<Table> = DummyDynamoDBRepository()
    override fun buildDynamoDBRepository(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        return build_returnValue
    }

    var buildDynamoDBEnhancedRepository_returnValue: NoSQLEnhancedRepository = DummyDynamoDBEnhancedRepository()
    override fun buildDynamoDBEnhancedRepository(): NoSQLEnhancedRepository {
        return buildDynamoDBEnhancedRepository_returnValue
    }
}
