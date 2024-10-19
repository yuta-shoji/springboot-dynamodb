package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import com.sjyt.springboot_dynamodb.model.Order
import org.springframework.stereotype.Repository

interface OrderRepository {
    fun findAllOrders(): List<Order>
    fun findOrderById(id: String): Order?
    fun findOrdersByProductName(productName: String): List<Order>
    fun findOrdersByUserEmail(email: String): List<Order>
}

@Repository
class DefaultOrderRepository(
    private val dynamoDBRepository: NoSQLRepository<MainTableEntity>
): OrderRepository {
    override fun findAllOrders(): List<Order> {
        return dynamoDBRepository
            .findAllByPK("ORDER")
            .toOrders()
    }

    override fun findOrderById(id: String): Order? {
        return dynamoDBRepository
            .findByPKAndSK("ORDER", id)
            .toOrderOrNull()
    }

    override fun findOrdersByProductName(productName: String): List<Order> {
        val gsi = GSI("ProductNameGSI", productName)

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

    private fun MainTableEntity?.toOrderOrNull(): Order? {
        this ?: return null
        return Order(
            id = this.sk,
            productName = this.productName,
            amount = this.amount,
            place = this.place,
        )
    }

    private fun List<MainTableEntity>.toOrders(): List<Order> {
        return this.map {
            Order(
                id = it.sk,
                productName = it.productName,
                amount = it.amount,
                place = it.place,
            )
        }
    }
}
