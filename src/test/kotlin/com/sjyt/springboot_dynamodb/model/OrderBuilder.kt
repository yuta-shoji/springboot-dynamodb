package com.sjyt.springboot_dynamodb.model


class OrderBuilder {
    var id: String = ""
    var productName: String = ""
    var email: String = ""
    var amount: Int = 0
    var place: Int = 0

    companion object {
        fun build(block: OrderBuilder.() -> Unit) = OrderBuilder().apply(block).build()
    }

    fun build(): Order {
        return Order(
            id,
            productName,
            email,
            amount,
            place,
        )
    }
}
