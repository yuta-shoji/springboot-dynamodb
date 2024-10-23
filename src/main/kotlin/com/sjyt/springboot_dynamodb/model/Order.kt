package com.sjyt.springboot_dynamodb.model

data class Order(
    val id: String,
    val productName: String,
    val email: String,
    val amount: Int,
    val place: Int,
)
