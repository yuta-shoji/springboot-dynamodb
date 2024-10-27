package com.sjyt.springboot_dynamodb.model

import java.time.LocalDateTime

data class EventsByEventTypeAndDatesBetweenRequestBody(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
