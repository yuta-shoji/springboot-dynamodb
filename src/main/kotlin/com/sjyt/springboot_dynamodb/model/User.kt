package com.sjyt.springboot_dynamodb.model

import com.sjyt.springboot_dynamodb.entity.MainTableEntity

data class User(
    val name: String,
    val email: String,
    val age: Int,
) {
    fun toMainTableEntity(): MainTableEntity {
        return MainTableEntity(
            pk = "USER",
            sk = email,
            emailLsiSk = "",
            userName = name,
            age = age,
        )
    }
}

