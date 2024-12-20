package com.sjyt.springboot_dynamodb.entity

import com.sjyt.springboot_dynamodb.model.Order
import com.sjyt.springboot_dynamodb.model.User
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*

@DynamoDbBean
data class MainTableEntity(
    @get:DynamoDbPartitionKey
    var pk: String,

    @get:DynamoDbSortKey
    var sk: String,

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

fun MainTableEntity.toOrder(): Order {
    return Order(
        id = this.sk,
        productName = this.productName,
        email = this.emailLsiSk,
        amount = this.amount ?: 0,
        place = this.place ?: 0,
    )
}

fun List<MainTableEntity>.toOrders(): List<Order> {
    return this.map {
        Order(
            id = it.sk,
            productName = it.productName,
            email = it.emailLsiSk,
            amount = it.amount ?: 0,
            place = it.place ?: 0,
        )
    }
}

fun MainTableEntity.toUser(): User {
    return User(
        name = this.userName ?: "",
        email = this.emailLsiSk,
        age = this.age ?: 0
    )
}

fun List<MainTableEntity>.toUsers(): List<User> {
    return this.map {
        User(
            name = it.userName ?: "",
            email = it.emailLsiSk,
            age = it.age ?: 0
        )
    }
}