package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.*
import com.sjyt.springboot_dynamodb.model.*
import com.sjyt.springboot_dynamodb.model.request.BatchResource
import com.sjyt.springboot_dynamodb.model.request.PrimaryKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient

interface OrderRepository {
    fun findAllOrders(): List<MainTableEntity>
    fun findOrderById(id: String): MainTableEntity?
    fun findOrdersByProductName(productName: String): List<MainTableEntity>
    fun findOrdersByUserEmail(email: String): List<MainTableEntity>
    fun saveOrder(order: Order)
    fun saveOrderAndEventInTransact(order: Order, event: Event)
    fun batchGetOrderAndEvent(
        orderPrimaryKeys: List<PrimaryKey<String, String>>,
        eventPrimaryKeys: List<PrimaryKey<String, String>>,
    ): MainAndEventTableEntities
}

@Repository
class DefaultOrderRepository(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    @Value("\${dynamodb.table-name-suffix}")
    tableNameSuffix: String,
) :
    OrderRepository,
    DynamoDBRepository<MainTableEntity, String, String>(dynamoDbEnhancedClient, tableNameSuffix)
{
    override fun findAllOrders(): List<MainTableEntity> {
        return this
            .findAllByPK("ORDER")
    }

    override fun findOrderById(id: String): MainTableEntity? {
        return this
            .findByPrimaryKeys("ORDER", id)
    }

    override fun findOrdersByProductName(productName: String): List<MainTableEntity> {
        val gsi = GSI.withoutSk("ProductNameGSI", productName)

        return this
            .findAllByGSI(gsi)
    }

    override fun findOrdersByUserEmail(email: String): List<MainTableEntity> {
        val lsi = LSI("EmailLSI", "ORDER", email)

        return this
            .findAllByLSI(lsi)
    }

    override fun saveOrder(order: Order) {
        this.save(order.toMainTableEntity())
    }

    override fun saveOrderAndEventInTransact(order: Order, event: Event) {
        val items = listOf(
            order.toMainTableEntity(),
            event.toEventTableEntity(),
        )
        this.saveInTransaction(items)
    }

    override fun batchGetOrderAndEvent(
        orderPrimaryKeys: List<PrimaryKey<String, String>>,
        eventPrimaryKeys: List<PrimaryKey<String, String>>
    ): MainAndEventTableEntities {
        val resources = listOf(
            BatchResource(
                MainTableEntity::class.java,
                orderPrimaryKeys,
            ),
            BatchResource(
                EventTableEntity::class.java,
                eventPrimaryKeys,
            ),
        )
        val batchResponses = this
            .batchGetItems(resources)
        val orders = batchResponses
            .asSequence()
            .flatMap { it.items.asSequence() }
            .filterIsInstance<MainTableEntity>()
            .toList()

        val events = batchResponses
            .asSequence()
            .flatMap { it.items.asSequence() }
            .filterIsInstance<EventTableEntity>()
            .toList()

        return MainAndEventTableEntities(
            orders,
            events,
        )
    }
}
