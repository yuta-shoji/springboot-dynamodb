package com.sjyt.springboot_dynamodb.entity

import com.sjyt.springboot_dynamodb.annotation.DynamoDBTableEntity
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.EventType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalDateTime

@DynamoDBTableEntity
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

fun List<EventTableEntity>.toEvents(): List<Event> {
    return this.map {
        Event(
            type = EventType.init(it.eventType),
            date = it.date
        )
    }
}

fun EventTableEntity.toEvent(): Event {
    return Event(
        type = EventType.init(this.eventType),
        date = this.date
    )
}