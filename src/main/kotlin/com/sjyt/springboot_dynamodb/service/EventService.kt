package com.sjyt.springboot_dynamodb.service

import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.EventType
import com.sjyt.springboot_dynamodb.repository.EventRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface EventService {
    fun findAllEvents(): List<Event>
    fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Event>
}

@Service
class DefaultEventService(
    private val eventRepository: EventRepository
): EventService {
    override fun findAllEvents(): List<Event> {
        return eventRepository.findAllEvents()
    }

    override fun findEventsByEventTypeAndDatesBetween(
        eventType: EventType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Event> {
        return eventRepository
            .findEventsByEventTypeAndDatesBetween(
                eventType,
                startDate,
                endDate,
            )
    }
}