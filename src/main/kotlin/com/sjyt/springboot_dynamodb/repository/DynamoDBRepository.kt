package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.util.stream.Collectors

interface NoSQLRepository<Table> {
    fun findAllByPK(pk: String): List<Table>
    fun findByPKAndSK(pk: String, sk: String): Table?
    fun findAllByGSI(gsi: SecondaryIndex): List<Table>
    fun findAllByLSI(lsi: SecondaryIndex): List<Table>
}

class DynamoDBRepository<Table>(
    private val dynamoDbTable: DynamoDbTable<Table>,
) : NoSQLRepository<Table> {
    override fun findAllByPK(pk: String): List<Table> {
        val queryConditional = QueryConditional
            .keyEqualTo(
                Key.builder()
                    .partitionValue(pk)
                    .build()
            )

        val request = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build()

        return dynamoDbTable
            .query(request)
            .stream()
            .flatMap { page -> page.items().stream() }
            .collect(Collectors.toList())
    }

    override fun findByPKAndSK(pk: String, sk: String): Table? {
        val key = Key.builder()
            .partitionValue(pk)
            .sortValue(sk)
            .build()

        return try {
            dynamoDbTable.getItem(key)
        } catch(e: Exception) {
            null
        }
    }

    override fun findAllByGSI(gsi: SecondaryIndex): List<Table> {
        val keyBuilder = Key.builder()
            .partitionValue(gsi.pk)

        gsi.sk ?.let { keyBuilder.sortValue(gsi.sk) }

        val queryConditional = QueryConditional
            .keyEqualTo(keyBuilder.build())

        return dynamoDbTable
            .index(gsi.indexName)
            .query(queryConditional)
            .stream()
            .flatMap { page -> page.items().stream() }
            .collect(Collectors.toList())
    }

    override fun findAllByLSI(lsi: SecondaryIndex): List<Table> {
        return findAllByGSI(lsi)
    }
}
