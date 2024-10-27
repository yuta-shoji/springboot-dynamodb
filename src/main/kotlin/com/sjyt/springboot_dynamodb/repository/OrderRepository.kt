package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.config.dynamodb.NoSQLFactory
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import com.sjyt.springboot_dynamodb.model.Order
import org.springframework.stereotype.Repository

interface OrderRepository: BaseRepository, EnhancedRepository {
    fun findAllOrders(): List<Order>
    fun findOrderById(id: String): Order?
    fun findOrdersByProductName(productName: String): List<Order>
    fun findOrdersByUserEmail(email: String): List<Order>
    fun saveOrder(order: Order)
    fun saveOrderAndEventInTransact(order: Order, event: Event)
}

@Repository
class DefaultOrderRepository(
    dynamoDBFactory: NoSQLFactory<MainTableEntity>,
): OrderRepository {
    override val dynamoDBRepository = dynamoDBFactory.buildDynamoDBRepository(MainTableEntity::class.java)
    override val dynamoDBEnhancedRepository: NoSQLEnhancedRepository = dynamoDBFactory.buildDynamoDBEnhancedRepository()

    override fun findAllOrders(): List<Order> {
        return dynamoDBRepository
            .findAllByPK("ORDER")
            .toOrders()
    }

    override fun findOrderById(id: String): Order? {
        return dynamoDBRepository
            .findByPrimaryKeys("ORDER", id)
            .toOrderOrNull()
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

    override fun saveOrderAndEventInTransact(order: Order, event: Event) {
        val items = listOf(
            order.toMainTableEntity(),
            event.toEventTableEntity(),
        )
        dynamoDBEnhancedRepository.saveInTransaction(items)
    }

    private fun MainTableEntity?.toOrderOrNull(): Order? {
        this ?: return null
        return Order(
            id = this.sk,
            productName = this.productName,
            email = this.emailLsiSk,
            amount = this.amount ?: 0,
            place = this.place ?: 0,
        )
    }

    private fun List<MainTableEntity>.toOrders(): List<Order> {
        return this.map {
            Order(
                id = it.sk,
                productName = it.productName,
                email = it.emailLsiSk,
                amount = it.amount ?: 0,
                place = it.place ?: 0,
            )
        }
    }
}
