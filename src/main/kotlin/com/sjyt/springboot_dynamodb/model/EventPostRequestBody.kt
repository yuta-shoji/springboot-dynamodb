package com.sjyt.springboot_dynamodb.model

import java.time.LocalDateTime

data class EventPostRequestBody(
    val type: EventType,
    val date: LocalDateTime,
)

fun EventPostRequestBody.toEvent(): Event {
    return Event(
        type = this.type,
        date = this.date,
    )
}