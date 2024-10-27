package com.sjyt.springboot_dynamodb.model

data class TransactRequestBody(
    val order: OrderRequestBody,
    val event: EventPostRequestBody,
)
