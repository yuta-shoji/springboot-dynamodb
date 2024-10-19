package com.sjyt.springboot_dynamodb.controller

import com.sjyt.springboot_dynamodb.model.Order
import com.sjyt.springboot_dynamodb.service.OrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping
    fun findAllOrders(): List<Order> {
        return orderService.findAllOrders()
    }

    @GetMapping("/{id}")
    fun findOrderById(@PathVariable id: String): Order? {
        return orderService.findOrderById(id)
    }

    @GetMapping("/productName/{productName}")
    fun findOrdersByProductName(@PathVariable productName: String): List<Order> {
        return orderService.findOrdersByProductName(productName)
    }

    @GetMapping("/email/{email}")
    fun findOrdersByUserEmail(@PathVariable email: String): List<Order> {
        return orderService.findOrdersByUserEmail(email)
    }
}