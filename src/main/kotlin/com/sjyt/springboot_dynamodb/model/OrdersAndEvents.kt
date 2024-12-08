package com.sjyt.springboot_dynamodb.model

data class OrdersAndEvents(
    val orders: List<Order>,
    val events: List<Event>,
)
