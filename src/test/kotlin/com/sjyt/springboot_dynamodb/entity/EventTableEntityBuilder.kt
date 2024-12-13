package com.sjyt.springboot_dynamodb.entity

import java.time.LocalDateTime

class EventTableEntityBuilder {
    var eventType: String = ""
    var date: LocalDateTime = LocalDateTime.now()

    companion object {
        fun build(block: EventTableEntityBuilder.() -> Unit) = EventTableEntityBuilder().apply(block).build()
    }

    fun build(): EventTableEntity {
        return EventTableEntity(
            eventType,
            date,
        )
    }
}
