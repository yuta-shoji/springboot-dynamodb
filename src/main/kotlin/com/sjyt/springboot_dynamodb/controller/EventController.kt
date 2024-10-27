package com.sjyt.springboot_dynamodb.controller

import com.sjyt.springboot_dynamodb.model.Event
import com.sjyt.springboot_dynamodb.model.EventsByEventTypeAndDatesBetweenRequestBody
import com.sjyt.springboot_dynamodb.model.EventType
import com.sjyt.springboot_dynamodb.service.EventService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService
) {
    @GetMapping
    fun getAllEvents(): List<Event> {
        return eventService.findAllEvents()
    }

    @GetMapping("/eventType/{eventType}")
    fun getAllEventsByEventTypeAndDatesBetween(
        @PathVariable eventType: String,
        @RequestBody eventBody: EventsByEventTypeAndDatesBetweenRequestBody,
    ): List<Event> {
        return eventService
            .findEventsByEventTypeAndDatesBetween(
                EventType.init(eventType),
                eventBody.startDate,
                eventBody.endDate,
            )
    }
}