package com.sjyt.springboot_dynamodb.repository.doubles

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.NoSQLEnhancedRepository

class DummyDynamoDBEnhancedRepository: NoSQLEnhancedRepository {
    override fun saveInTransaction(items: List<TableEntity>) {
    }
}