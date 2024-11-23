package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.entity.toEvents
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.EventType
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface EventRepository {
    fun findAllEvents(): List<Event>
    fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Event>
}

@Repository
class DefaultEventRepository(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Qualifier("eventTableRepository")
    private val dynamoDBRepository: DynamoDBRepository<EventTableEntity>
) : EventRepository {
    override fun findAllEvents(): List<Event> {
        return dynamoDBRepository.findAll().toEvents()
    }

    override fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Event> {
        return dynamoDBRepository
            .findAllByPKAndSKBetween(
                eventType.toString(),
                startDate.toString(),
                endDate.toString(),
            )
            .toEvents()
    }
}
