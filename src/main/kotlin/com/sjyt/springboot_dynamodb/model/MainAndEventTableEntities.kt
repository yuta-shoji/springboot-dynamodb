package com.sjyt.springboot_dynamodb.model

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.entity.toEvents
import com.sjyt.springboot_dynamodb.entity.toOrders

data class MainAndEventTableEntities(
    val mainTableEntities: List<MainTableEntity>,
    val eventTableEntities: List<EventTableEntity>,
) {
    fun toOrdersAndEvents(): OrdersAndEvents {
        return OrdersAndEvents(
            this.mainTableEntities.toOrders(),
            this.eventTableEntities.toEvents(),
        )
    }
}