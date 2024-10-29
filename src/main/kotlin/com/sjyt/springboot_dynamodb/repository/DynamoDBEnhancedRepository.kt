package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.extension.setPrimaryKeys
import com.sjyt.springboot_dynamodb.model.request.BatchResource
import com.sjyt.springboot_dynamodb.model.response.BatchResponse
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch

interface NoSQLEnhancedRepository {
    fun saveInTransaction(items: List<TableEntity>)
    fun batchGetItems(resources: List<BatchResource<*, *>>): List<BatchResponse>
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

    override fun batchGetItems(
        resources: List<BatchResource<*, *>>
    ): List<BatchResponse> {
        val readBatches = resources.map { resource ->
            val instance = resource.tableEntity.getConstructor().newInstance()
            val dynamoDBTable = dynamoDbEnhancedClient.table(
                "${instance.tableName}_$environment",
                TableSchema.fromClass(resource.tableEntity)
            )
            val readBatchBuilder = ReadBatch
                .builder(resource.tableEntity)
                .mappedTableResource(dynamoDBTable as MappedTableResource<TableEntity>)

            resource.primaryKeys.forEach { primaryKey ->
                readBatchBuilder.addGetItem(
                    Key.builder()
                        .setPrimaryKeys(primaryKey.pk, primaryKey.sk)
                        .build()
                )
            }

            readBatchBuilder.build()
        }

        val batchGetResultPageIterable = dynamoDbEnhancedClient.batchGetItem {
            it.readBatches(readBatches)
        }

        return resources.map { resource ->
            val instance = resource.tableEntity.getConstructor().newInstance()
            val dynamoDBTable = dynamoDbEnhancedClient.table(
                "${instance.tableName}_$environment",
                TableSchema.fromBean(resource.tableEntity)
            )

            val entities = batchGetResultPageIterable
                .resultsForTable(dynamoDBTable)
                .map { it }
            BatchResponse(items = entities)
        }
    }
}
