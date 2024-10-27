package com.sjyt.springboot_dynamodb.controller

import com.sjyt.springboot_dynamodb.model.*
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

    @PostMapping("/transact")
    fun saveOrderAndEventInTransact(
        @RequestBody body: TransactRequestBody,
    ) {
        orderService.saveOrderAndEventInTransact(
            body.order.toOrder(),
            body.event.toEvent(),
        )
    }
}