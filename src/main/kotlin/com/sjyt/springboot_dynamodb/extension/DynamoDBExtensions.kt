package com.sjyt.springboot_dynamodb.extension

import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun <Table> PageIterable<Table>.toEntities(): List<Table> {
    return this.flatMap { it.items() }
}

fun <Table> SdkIterable<Page<Table>>.toEntities(): List<Table> {
    return this.flatMap { it.items() }
}

fun <PK, SK> Key.Builder.setPrimaryKeys(pk: PK, sk: SK?): Key.Builder {
    this
        .setPK(pk)
        .setSK(sk)
    return this
}

fun <PK, SK>Key.Builder.setPrimaryKeys(
    secondaryIndex: SecondaryIndex<PK, SK>
): Key.Builder {
    this
        .setPK(secondaryIndex.pk)
        .setSK(secondaryIndex.sk)
    return this
}

fun <PK> Key.Builder.setPK(pk: PK): Key.Builder {
    when (pk) {
        is String -> this.partitionValue(pk)
        is Number -> this.partitionValue(pk)
        is SdkBytes -> this.partitionValue(pk)
        is AttributeValue -> this.partitionValue(pk)
        else -> throw IllegalArgumentException("Unsupported type for primary key")
    }
    return this
}

private fun <SK> Key.Builder.setSK(sk: SK?): Key.Builder {
    sk ?: return this

    when (sk) {
        is String -> this.sortValue(sk)
        is Number -> this.sortValue(sk)
        is SdkBytes -> this.sortValue(sk)
        is AttributeValue -> this.sortValue(sk)
        else -> throw IllegalArgumentException("Unsupported type for primary key")
    }
    return this
}
