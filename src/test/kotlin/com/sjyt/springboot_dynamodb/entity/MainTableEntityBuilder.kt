package com.sjyt.springboot_dynamodb.entity

class MainTableEntityBuilder {
    var pk: String = ""
    var sk: String = ""
    var productName: String = ""
    var emailLsiSk: String = ""
    var amount: Int? = null
    var place: Int? = null
    var userName: String? = null
    var age: Int? = null

    companion object {
        fun build(block: MainTableEntityBuilder.() -> Unit) = MainTableEntityBuilder().apply(block).build()
    }

    fun build(): MainTableEntity {
        return MainTableEntity(
            pk,
            sk,
            productName,
            emailLsiSk,
            amount,
            place,
            userName,
            age,
        )
    }
}
