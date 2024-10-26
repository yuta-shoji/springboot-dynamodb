package com.sjyt.springboot_dynamodb.model

import com.sjyt.springboot_dynamodb.entity.MainTableEntity

data class Order(
    val id: String,
    val productName: String,
    val email: String,
    val amount: Int,
    val place: Int,
) {
    fun toMainTableEntity(): MainTableEntity {
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
