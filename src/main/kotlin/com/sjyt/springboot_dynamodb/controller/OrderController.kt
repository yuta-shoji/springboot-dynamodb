package com.sjyt.springboot_dynamodb.controller

import com.sjyt.springboot_dynamodb.model.Order
import com.sjyt.springboot_dynamodb.model.OrderRequestBody
import com.sjyt.springboot_dynamodb.model.toOrder
import com.sjyt.springboot_dynamodb.service.OrderService
import org.springframework.web.bind.annotation.*

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

    @PutMapping
    fun saveNewOrder(@RequestBody body: OrderRequestBody) {
        orderService.saveOrder(body.toOrder())
    }
}