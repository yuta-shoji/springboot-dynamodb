package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface NoSQLEnhancedRepository {
    fun saveInTransaction(items: List<TableEntity>)
}

class DynamoDBEnhancedRepository(
    private val environment: String,
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) : NoSQLEnhancedRepository {
    override fun saveInTransaction(items: List<TableEntity>) {
        dynamoDbEnhancedClient.transactWriteItems {
            items.forEach { item ->
                val instance = item.javaClass.getConstructor().newInstance()
                val dynamoDBTable = dynamoDbEnhancedClient.table(
                    "${instance.tableName}_$environment",
                    TableSchema.fromBean(item.javaClass)
                )
                it.addPutItem(dynamoDBTable, item)
            }
        }
    }
}