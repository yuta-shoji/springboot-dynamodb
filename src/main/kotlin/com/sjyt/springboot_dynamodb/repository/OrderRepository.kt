package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.entity.toOrder
import com.sjyt.springboot_dynamodb.entity.toOrders
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import com.sjyt.springboot_dynamodb.model.Order
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

interface OrderRepository {
    fun findAllOrders(): List<Order>
    fun findOrderById(id: String): Order?
    fun findOrdersByProductName(productName: String): List<Order>
    fun findOrdersByUserEmail(email: String): List<Order>
    fun saveOrder(order: Order)
//    fun saveOrderAndEventInTransact(order: Order, event: Event)
//    fun batchGetOrderAndEvent(
//        orderPrimaryKeys: List<PrimaryKey<String, String>>,
//        eventPrimaryKeys: List<PrimaryKey<String, String>>,
//    ): OrdersAndEvents
}

data class OrdersAndEvents(
    val orders: List<Order>,
    val events: List<Event>,
)

@Repository
class DefaultOrderRepository(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Qualifier("mainTableEntity")
    private val dynamoDBRepository: NoSQLRepository<MainTableEntity>,
) : OrderRepository {
    override fun findAllOrders(): List<Order> {
        return dynamoDBRepository
            .findAllByPK("ORDER")
            .toOrders()
    }

    override fun findOrderById(id: String): Order? {
        return dynamoDBRepository
            .findByPrimaryKeys("ORDER", id)
            ?.toOrder()
    }

    override fun findOrdersByProductName(productName: String): List<Order> {
        val gsi = GSI.withoutSk("ProductNameGSI", productName)

        return dynamoDBRepository
            .findAllByGSI(gsi)
            .toOrders()
    }

    override fun findOrdersByUserEmail(email: String): List<Order> {
        val lsi = LSI("EmailLSI", "ORDER", email)

        return dynamoDBRepository
            .findAllByLSI(lsi)
            .toOrders()
    }

    override fun saveOrder(order: Order) {
        dynamoDBRepository.save(order.toMainTableEntity())
    }

//    override fun saveOrderAndEventInTransact(order: Order, event: Event) {
//        val items = listOf(
//            order.toMainTableEntity(),
//            event.toEventTableEntity(),
//        )
//        dynamoDBEnhancedRepository.saveInTransaction(items)
//    }
//
//    override fun batchGetOrderAndEvent(
//        orderPrimaryKeys: List<PrimaryKey<String, String>>,
//        eventPrimaryKeys: List<PrimaryKey<String, String>>
//    ): OrdersAndEvents {
//        val resources = listOf(
//            BatchResource(
//                MainTableEntity::class.java,
//                orderPrimaryKeys,
//            ),
//            BatchResource(
//                EventTableEntity::class.java,
//                eventPrimaryKeys,
//            ),
//        )
//        val batchResponses = dynamoDBEnhancedRepository
//            .batchGetItems(resources)
//        val orders2 = batchResponses
//            .asSequence()
//            .flatMap { it.items.asSequence() }
//            .filterIsInstance<MainTableEntity>()
//            .map { it.toOrder() }
//            .toList()
//
//        val events = batchResponses
//            .asSequence()
//            .flatMap { it.items.asSequence() }
//            .filterIsInstance<EventTableEntity>()
//            .map { it.toEvent() }
//            .toList()
//
//        return OrdersAndEvents(
//            orders = orders2,
//            events = events,
//        )
//    }
}
