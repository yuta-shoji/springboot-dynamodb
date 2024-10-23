package com.sjyt.springboot_dynamodb.service

import com.sjyt.springboot_dynamodb.model.Order
import com.sjyt.springboot_dynamodb.repository.OrderRepository
import org.springframework.stereotype.Service

interface OrderService {
    fun findAllOrders(): List<Order>
    fun findOrderById(id: String): Order?
    fun findOrdersByProductName(productName: String): List<Order>
    fun findOrdersByUserEmail(email: String): List<Order>
    fun saveOrder(order: Order)
}

@Service
class DefaultOrderService(
    private val orderRepository: OrderRepository
) : OrderService {
    override fun findAllOrders(): List<Order> {
        // Implement some business logic here
        return orderRepository.findAllOrders()
    }

    override fun findOrderById(id: String): Order? {
        // Implement some business logic here
        return orderRepository.findOrderById(id)
    }

    override fun findOrdersByProductName(productName: String): List<Order> {
        // Implement some business logic here
        return orderRepository.findOrdersByProductName(productName)
    }

    override fun findOrdersByUserEmail(email: String): List<Order> {
        // Implement some business logic here
        return orderRepository.findOrdersByUserEmail(email)
    }

    override fun saveOrder(order: Order) {
        // Implement some business logic here
        orderRepository.saveOrder(order)
    }
}