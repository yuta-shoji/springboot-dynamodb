package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.config.dynamodb.DynamoDBRepositoryFactory
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import com.sjyt.springboot_dynamodb.model.Order
import org.springframework.stereotype.Repository

interface OrderRepository: BaseRepository {
    fun findAllOrders(): List<Order>
    fun findOrderById(id: String): Order?
    fun findOrdersByProductName(productName: String): List<Order>
    fun findOrdersByUserEmail(email: String): List<Order>
    fun saveOrder(order: Order)
}

@Repository
class DefaultOrderRepository(
    dynamoDBRepositoryFactory: DynamoDBRepositoryFactory
): OrderRepository {
    override val dynamoDBRepository = dynamoDBRepositoryFactory
        .build<MainTableEntity>()

    override fun findAllOrders(): List<Order> {
        return dynamoDBRepository
            .findAllByPK("ORDER")
            .toOrders()
    }

    override fun findOrderById(id: String): Order? {
        return dynamoDBRepository
            .findByPartitionKeys("ORDER", id)
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

    private fun Order.toMainTableEntity(): MainTableEntity {
        return MainTableEntity(
            pk = "ORDER",
            sk = this.id,
            productName = this.productName,
            emailLsiSk = this.email,
            amount = this.amount,
            place = this.place,
        )
    }
}
