package com.sjyt.springboot_dynamodb.service

import com.sjyt.springboot_dynamodb.entity.toOrder
import com.sjyt.springboot_dynamodb.entity.toOrders
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.Order
import com.sjyt.springboot_dynamodb.model.OrdersAndEvents
import com.sjyt.springboot_dynamodb.model.request.PrimaryKey
import com.sjyt.springboot_dynamodb.repository.OrderRepository
import org.springframework.stereotype.Service

interface OrderService {
    fun findAllOrders(): List<Order>
    fun findOrderById(id: String): Order?
    fun findOrdersByProductName(productName: String): List<Order>
    fun findOrdersByUserEmail(email: String): List<Order>
    fun saveOrder(order: Order)
    fun saveOrderAndEventInTransact(order: Order, event: Event)
    fun batchGetOrderAndEvent(
        orderPrimaryKeys: List<PrimaryKey<String, String>>,
        eventPrimaryKeys: List<PrimaryKey<String, String>>,
    ): OrdersAndEvents
}

@Service
class DefaultOrderService(
    private val orderRepository: OrderRepository
) : OrderService {
    override fun findAllOrders(): List<Order> {
        // Implement some business logic here
        return orderRepository.findAllOrders().toOrders()
    }

    override fun findOrderById(id: String): Order? {
        // Implement some business logic here
        return orderRepository.findOrderById(id)?.toOrder()
    }

    override fun findOrdersByProductName(productName: String): List<Order> {
        // Implement some business logic here
        return orderRepository.findOrdersByProductName(productName).toOrders()
    }

    override fun findOrdersByUserEmail(email: String): List<Order> {
        // Implement some business logic here
        return orderRepository.findOrdersByUserEmail(email).toOrders()
    }

    override fun saveOrder(order: Order) {
        // Implement some business logic here
        orderRepository.saveOrder(order)
    }

    override fun saveOrderAndEventInTransact(order: Order, event: Event) {
        // Implement some business logic here
        orderRepository.saveOrderAndEventInTransact(order, event)
    }

    override fun batchGetOrderAndEvent(
        orderPrimaryKeys: List<PrimaryKey<String, String>>,
        eventPrimaryKeys: List<PrimaryKey<String, String>>
    ): OrdersAndEvents {
        return orderRepository
            .batchGetOrderAndEvent(orderPrimaryKeys, eventPrimaryKeys)
            .toOrdersAndEvents()
    }
}