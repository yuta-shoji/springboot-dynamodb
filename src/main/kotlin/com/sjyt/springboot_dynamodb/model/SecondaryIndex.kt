package com.sjyt.springboot_dynamodb.model

interface SecondaryIndex {
    val indexName: String
    val pk: String
    val sk: String?
}

data class GSI(
    override val indexName: String,
    override val pk: String,
    override val sk: String? = null,
): SecondaryIndex


data class LSI(
    override val indexName: String,
    override val pk: String,
    override val sk: String,
): SecondaryIndex
