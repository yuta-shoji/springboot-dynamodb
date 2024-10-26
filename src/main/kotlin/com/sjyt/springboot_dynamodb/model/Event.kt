package com.sjyt.springboot_dynamodb.model

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import java.time.LocalDateTime

data class Event(
    val type: EventType,
    val date: LocalDateTime,
) {
    fun toEventTableEntity(): EventTableEntity {
        return EventTableEntity(
            eventType = type.toString(),
            date = date,
        )
    }
}
