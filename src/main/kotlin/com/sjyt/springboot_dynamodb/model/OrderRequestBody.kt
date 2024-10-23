package com.sjyt.springboot_dynamodb.model

import java.util.*

data class OrderRequestBody(
    val productName: String,
    val email: String,
    val amount: Int,
    val place: Int,
)

fun OrderRequestBody.toOrder(): Order {
    return Order(
        id = UUID.randomUUID().toString(),
        productName = this.productName,
        email = this.email,
        amount = this.amount,
        place = this.place,
    )
}
