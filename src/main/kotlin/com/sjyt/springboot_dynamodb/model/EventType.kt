package com.sjyt.springboot_dynamodb.model

enum class EventType {
    CLICK,
    SEND,
    UNKNOWN;

    companion object {
        fun init(eventTypeString: String): EventType {
            return when (eventTypeString) {
                "CLICK" -> CLICK
                "SEND" -> SEND
                else -> UNKNOWN
            }
        }
    }
}