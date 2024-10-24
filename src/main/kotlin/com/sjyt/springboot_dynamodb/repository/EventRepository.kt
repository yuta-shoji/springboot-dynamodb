package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.config.dynamodb.DynamoDBRepositoryFactory
import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.EventType
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface EventRepository: BaseRepository {
    fun findAllEvents(): List<Event>
    fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Event>
}

@Repository
class DefaultEventRepository(
    dynamoDBRepositoryFactory: DynamoDBRepositoryFactory
) : EventRepository {
    override val dynamoDBRepository = dynamoDBRepositoryFactory
        .build<EventTableEntity>()

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

    private fun List<EventTableEntity>.toEvents(): List<Event> {
        return this.map {
            Event(
                type = EventType.init(it.eventType),
                date = it.date
            )
        }
    }
}
