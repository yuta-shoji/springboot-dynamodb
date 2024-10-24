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

    var amount: Int? = null,

    var place: Int? = null,

    var userName: String? = null,

    var age: Int? = null,
): TableEntity {
    override val tableName: String
        get() = "main_table"
}