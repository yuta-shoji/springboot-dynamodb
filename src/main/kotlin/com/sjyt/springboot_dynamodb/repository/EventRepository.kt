package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.EventType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import java.time.LocalDateTime

interface EventRepository {
    fun findAllEvents(): List<EventTableEntity>
    fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<EventTableEntity>
    fun saveEvent(event: Event)
}

@Repository
class DefaultEventRepository(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    @Value("\${dynamodb.table-name-suffix}")
    tableNameSuffix: String,
) :
    EventRepository,
    DynamoDBRepository<EventTableEntity, String, String>(
        dynamoDbEnhancedClient,
        tableNameSuffix
    )
{
    override fun findAllEvents(): List<EventTableEntity> {
        return this.findAll()
    }

    override fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<EventTableEntity> {
        return this
            .findAllByPKAndSKBetween(
                eventType.toString(),
                startDate.toString(),
                endDate.toString(),
            )
    }

    override fun saveEvent(event: Event) {
        this.save(event.toEventTableEntity())
    }
}
