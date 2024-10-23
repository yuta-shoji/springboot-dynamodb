package com.sjyt.springboot_dynamodb.model

import jakarta.annotation.Nullable

interface SecondaryIndex<PK, SK> {
    val indexName: String
    val pk: PK
    val sk: SK?
}

data class GSI<PK, SK>(
    override val indexName: String,
    override val pk: PK,
    override val sk: SK? = null,
): SecondaryIndex<PK, SK?> {
    companion object {
        fun <PK>withoutSk(
            indexName: String,
            pk: PK,
        ): GSI<PK, Nullable> {
            return GSI(indexName, pk, sk = null)
        }
    }
}


data class LSI<PK, SK>(
    override val indexName: String,
    override val pk: PK,
    override val sk: SK,
): SecondaryIndex<PK, SK>
