package com.sjyt.springboot_dynamodb.model.request

data class BatchRequestBodyWithOrderAndEvent(
    val orderPrimaryKeys: List<PrimaryKey<String, String>>,
    val eventPrimaryKeys: List<PrimaryKey<String, String>>,
)