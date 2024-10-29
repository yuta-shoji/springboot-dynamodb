package com.sjyt.springboot_dynamodb.model.request

data class PrimaryKey<PK, SK>(
    val pk: PK,
    val sk: SK?,
)