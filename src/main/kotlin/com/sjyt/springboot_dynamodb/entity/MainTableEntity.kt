package com.sjyt.springboot_dynamodb.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*

@DynamoDbBean
data class MainTableEntity(
    @get:DynamoDbPartitionKey
    var pk: String = "",

    @get:DynamoDbSortKey
    var sk: String = "",

    @get:DynamoDbSecondaryPartitionKey(indexNames = ["ProductNameGSI"])
    var productName: String = "",

    @get:DynamoDbSecondarySortKey(indexNames = ["EmailLSI"])
    var emailLsiSk: String = "",

    var amount: Int = 0,

    var place: Int = 0,

    var userName: String = "",

    var age: Int = 0,
)