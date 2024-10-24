package com.sjyt.springboot_dynamodb.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalDateTime

@DynamoDbBean
data class EventTableEntity(
    @get:DynamoDbPartitionKey
    var eventType: String = "",

    @get:DynamoDbSortKey
    var date: LocalDateTime = LocalDateTime.now(),
) : TableEntity {
    override val tableName: String
        get() = "event_table"
}