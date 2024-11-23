package com.sjyt.springboot_dynamodb.repository.doubles

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.model.request.BatchResource
import com.sjyt.springboot_dynamodb.model.response.BatchResponse
import com.sjyt.springboot_dynamodb.repository.NoSQLEnhancedRepository

class DummyDynamoDBEnhancedRepository: NoSQLEnhancedRepository {
    override fun saveInTransaction(items: List<TableEntity>) {
    }

    override fun batchGetItems(resources: List<BatchResource<*, *>>): List<BatchResponse> {
        return emptyList()
    }
}