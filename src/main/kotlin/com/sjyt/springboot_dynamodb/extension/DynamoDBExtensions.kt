package com.sjyt.springboot_dynamodb.extension

import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.Key

fun <Table> PageIterable<Table>.toEntities(): List<Table> {
    return this.flatMap { it.items() }
}

fun <Table> SdkIterable<Page<Table>>.toEntities(): List<Table> {
    return this.flatMap { it.items() }
}

fun Key.Builder.setPrimaryKey(pk: String, sk: String? = null): Key.Builder {
    this
        .partitionValue(pk)
        .sortValueOrNull(sk)
    return this
}

fun Key.Builder.setPrimaryKey(secondaryIndex: SecondaryIndex): Key.Builder {
    this
        .partitionValue(secondaryIndex.pk)
        .sortValueOrNull(secondaryIndex.sk)
    return this
}

fun Key.Builder.sortValueOrNull(sk: String?): Key.Builder {
    sk ?.let { this.sortValue(sk) }
    return this
}