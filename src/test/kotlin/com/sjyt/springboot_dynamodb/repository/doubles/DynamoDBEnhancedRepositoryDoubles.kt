package com.sjyt.springboot_dynamodb.repository.doubles

import com.sjyt.springboot_dynamodb.repository.NoSQLEnhancedRepository
import com.sjyt.springboot_dynamodb.repository.TransactResource

class DummyDynamoDBEnhancedRepository: NoSQLEnhancedRepository {
    override fun saveInTransaction(resources: List<TransactResource>) {
    }
}