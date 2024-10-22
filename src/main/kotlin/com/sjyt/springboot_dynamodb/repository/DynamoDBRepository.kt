package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.extension.setPrimaryKey
import com.sjyt.springboot_dynamodb.extension.toEntities
import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

interface NoSQLRepository<Table> {
    fun findAll(): List<Table>
    fun findAllByPK(pk: String): List<Table>
    fun findAllByPKAndSKBetween(pk: String, startSk: String, endSk: String): List<Table>
    fun findByPKAndSK(pk: String, sk: String): Table?
    fun findAllByGSI(gsi: SecondaryIndex): List<Table>
    fun findAllByLSI(lsi: SecondaryIndex): List<Table>
}

class DynamoDBRepository<Table>(
    private val dynamoDbTable: DynamoDbTable<Table>,
) : NoSQLRepository<Table> {
    override fun findAll(): List<Table> {
        return dynamoDbTable
            .scan()
            .toEntities()
    }

    override fun findAllByPK(pk: String): List<Table> {
        val queryConditional = QueryConditional
            .keyEqualTo(
                Key.builder()
                    .setPrimaryKey(pk)
                    .build()
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun findAllByPKAndSKBetween(pk: String, startSk: String, endSk: String): List<Table> {
        val sortBetweenCondition = QueryConditional
            .sortBetween(
                Key.builder()
                    .setPrimaryKey(pk, startSk)
                    .build(),
                Key.builder()
                    .setPrimaryKey(pk, endSk)
                    .build()
            )
        return dynamoDbTable
            .query(sortBetweenCondition)
            .toEntities()
    }

    override fun findByPKAndSK(pk: String, sk: String): Table? {
        val key = Key.builder()
            .setPrimaryKey(pk, sk)
            .build()

        return try {
            dynamoDbTable.getItem(key)
        } catch(e: Exception) {
            null
        }
    }

    override fun findAllByGSI(gsi: SecondaryIndex): List<Table> {
        val queryConditional = QueryConditional
            .keyEqualTo(
                Key.builder()
                    .setPrimaryKey(gsi)
                    .build()
            )

        return dynamoDbTable
            .index(gsi.indexName)
            .query(queryConditional)
            .toEntities()
    }

    override fun findAllByLSI(lsi: SecondaryIndex): List<Table> {
        return findAllByGSI(lsi)
    }
}
