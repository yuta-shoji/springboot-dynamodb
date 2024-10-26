package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import kotlin.reflect.KClass

data class TransactResource<out Table : TableEntity>(
    val table: KClass<out Table>,
    val item: Table,
)

interface NoSQLEnhancedRepository {
    fun <Table : TableEntity> saveInTransaction(resources: List<TransactResource<Table>>)
}

class DynamoDBEnhancedRepository(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) : NoSQLEnhancedRepository {
    override fun <Table : TableEntity> saveInTransaction(resources: List<TransactResource<Table>>) {
        dynamoDbEnhancedClient.transactWriteItems {
            resources.forEach { resource ->
                val dynamoDBTable = dynamoDbEnhancedClient.table(
                    resource.item.tableName,
                    TableSchema.fromBean(resource.table.java)
                )
                it.addPutItem(dynamoDBTable, resource.item)
            }
        }
    }
}