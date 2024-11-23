package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

@Profile("local")
@Component
class DynamoDBInitializer(
    private val standardClient: DynamoDbClient,
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    @Value("\${dynamodb.table-name-suffix}")
    private val tableNameSuffix: String,
) {
    private val mainTable: DynamoDbTable<MainTableEntity> = dynamoDbEnhancedClient
        .table(
            "main_table_${tableNameSuffix}",
            TableSchema.fromBean(MainTableEntity::class.java)
        )
    private val eventTable: DynamoDbTable<EventTableEntity> = dynamoDbEnhancedClient
        .table(
            "event_table_${tableNameSuffix}",
            TableSchema.fromBean(EventTableEntity::class.java)
        )

    @PostConstruct
    fun initializeDynamoDB() {
        createInformationTable()
    }

    private fun createInformationTable() {
        try {
            mainTable
                .createTable { builder ->
                    builder.provisionedThroughput { throughput ->
                        throughput
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build()
                    }
                }

            waitForTableBecomeActive(mainTable)
        } catch (error: ResourceInUseException) {
            println("Main Table already exists...skip creating tables.")
        } catch (error: Exception) {
            println("Error creating Customer table")
        }

        try {
            eventTable
                .createTable { builder ->
                    builder.provisionedThroughput { throughput ->
                        throughput
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build()
                    }
                }

            waitForTableBecomeActive(eventTable)
        } catch (error: ResourceInUseException) {
            println("Event Table already exists...skip creating tables.")
        } catch (error: Exception) {
            println("Error creating Customer table")
        }
    }

    private fun <Table>waitForTableBecomeActive(dynamoDbTable: DynamoDbTable<Table>) {
        val waiter = standardClient.waiter()
        val request = DescribeTableRequest.builder()
            .tableName(dynamoDbTable.tableName())
            .build()

        try {
            waiter.waitUntilTableExists(request)
        } catch (error: Exception) {
            throw RuntimeException("Table did not become active within the specified time", error)
        }
    }
}