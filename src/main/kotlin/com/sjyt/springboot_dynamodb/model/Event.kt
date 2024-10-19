package com.sjyt.springboot_dynamodb.model

import java.time.LocalDateTime

data class Event(
    val type: EventType,
    val date: LocalDateTime,
)
