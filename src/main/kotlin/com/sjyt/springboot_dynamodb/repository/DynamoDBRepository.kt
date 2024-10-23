package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.extension.setPK
import com.sjyt.springboot_dynamodb.extension.setPrimaryKeys
import com.sjyt.springboot_dynamodb.extension.toEntities
import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

interface NoSQLRepository<Table> {
    fun findAll(): List<Table>
    fun <PK> findAllByPK(pk: PK): List<Table>
    fun <PK, SK> findAllByPKAndSKBetween(pk: PK, startSk: SK, endSk: SK): List<Table>
    fun <PK, SK> findByPartitionKeys(pk: PK, sk: SK): Table?
    fun <GSIPK, GSISK> findAllByGSI(gsi: SecondaryIndex<GSIPK, GSISK>): List<Table>
    fun <LSIPK, LSISK> findAllByLSI(lsi: SecondaryIndex<LSIPK, LSISK>): List<Table>
    fun save(item: Table)
}

class DynamoDBRepository<Table>(
    private val dynamoDbTable: DynamoDbTable<Table>,
) : NoSQLRepository<Table> {
    override fun findAll(): List<Table> {
        return dynamoDbTable
            .scan()
            .toEntities()
    }

    override fun <PK> findAllByPK(pk: PK): List<Table> {
        val queryConditional = QueryConditional
            .keyEqualTo(
                Key.builder()
                    .setPK(pk)
                    .build()
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun <PK, SK> findAllByPKAndSKBetween(
        pk: PK,
        startSk: SK,
        endSk: SK,
    ): List<Table> {
        val sortBetweenCondition = QueryConditional
            .sortBetween(
                Key.builder()
                    .setPrimaryKeys(pk, startSk)
                    .build(),
                Key.builder()
                    .setPrimaryKeys(pk, endSk)
                    .build(),
            )

        return dynamoDbTable
            .query(sortBetweenCondition)
            .toEntities()
    }

    override fun <PK, SK> findByPartitionKeys(pk: PK, sk: SK): Table? {
        val key = Key.builder()
            .setPrimaryKeys(pk, sk)
            .build()

        return try {
            dynamoDbTable.getItem(key)
        } catch (e: Exception) {
            null
        }
    }

    override fun <GSIPK, GSISK> findAllByGSI(
        gsi: SecondaryIndex<GSIPK, GSISK>
    ): List<Table> {
        return findAllBySecondaryIndex(gsi)
    }

    override fun <LSIPK, LSISK> findAllByLSI(
        lsi: SecondaryIndex<LSIPK, LSISK>
    ): List<Table> {
        return findAllBySecondaryIndex(lsi)
    }

    override fun save(item: Table) {
        dynamoDbTable.putItem(item)
    }

    private fun <PK, SK> findAllBySecondaryIndex(
        secondaryIndex: SecondaryIndex<PK, SK>
    ): List<Table> {
        val queryConditional = QueryConditional
            .keyEqualTo(
                Key.builder()
                    .setPrimaryKeys(secondaryIndex)
                    .build()
            )

        return dynamoDbTable
            .index(secondaryIndex.indexName)
            .query(queryConditional)
            .toEntities()
    }
}
