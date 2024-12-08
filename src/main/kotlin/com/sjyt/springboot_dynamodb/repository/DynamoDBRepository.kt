package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.extension.setPK
import com.sjyt.springboot_dynamodb.extension.setPrimaryKeys
import com.sjyt.springboot_dynamodb.extension.toEntities
import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import java.lang.reflect.ParameterizedType

interface NoSQLRepository<Table : TableEntity, PK, SK> {
    fun findAll(): List<Table>
    fun findAllWithLimit(limit: Int): List<Table>
    fun findAllByPK(pk: PK): List<Table>
    fun findAllByPKAndSKBetween(pk: PK, startSk: SK, endSk: SK): List<Table>
    fun findAllByPKAndSKBeginsWith(pk: PK, beginningOfSk: SK): List<Table>
    fun findAllByPKAndSKGreaterThan(pk: PK, sk: SK): List<Table>
    fun findAllByPKAndSKGreaterThanOrEqualTo(pk: PK, sk: SK): List<Table>
    fun findAllByPKAndSKLessThan(pk: PK, sk: SK): List<Table>
    fun findAllByPKAndSKLessThanOrEqualTo(pk: PK, sk: SK): List<Table>

    fun findByPrimaryKeys(pk: PK, sk: SK): Table?

    fun <GSIPK, GSISK> findAllByGSI(gsi: SecondaryIndex<GSIPK, GSISK>): List<Table>
    fun <LSIPK, LSISK> findAllByLSI(lsi: SecondaryIndex<LSIPK, LSISK>): List<Table>

    fun save(item: Table)
    fun delete(item: Table)
}

abstract class DynamoDBRepository<Table : TableEntity, PK, SK>(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    tableNameSuffix: String,
) :
    NoSQLRepository<Table, PK, SK>,
    DynamoDBEnhancedRepository(
        dynamoDbEnhancedClient,
        tableNameSuffix
    )
{
    private val dynamoDbTable: DynamoDbTable<Table>

    init {
        @Suppress("UNCHECKED_CAST")
        val tableEntityClass: Class<Table> = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<Table>

        val tableName = "${tableEntityClass.getDeclaredConstructor().newInstance().tableName}_$tableNameSuffix"
        this.dynamoDbTable = dynamoDbEnhancedClient.table(tableName, TableSchema.fromClass(tableEntityClass))
    }

    override fun findAll(): List<Table> {
        return dynamoDbTable.scan().toEntities()
    }

    override fun findAllWithLimit(limit: Int): List<Table> {
        val request = ScanEnhancedRequest.builder().limit(limit).build()
        return dynamoDbTable.scan(request).toEntities()
    }

    override fun findAllByPK(pk: PK): List<Table> {
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

    override fun findAllByPKAndSKBetween(pk: PK, startSk: SK, endSk: SK): List<Table> {
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

    override fun findAllByPKAndSKBeginsWith(
        pk: PK,
        beginningOfSk: SK
    ): List<Table> {
        val queryConditional = QueryConditional
            .sortBeginsWith(
                Key.builder()
                    .setPrimaryKeys(pk, beginningOfSk)
                    .build(),
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun findAllByPKAndSKGreaterThan(pk: PK, sk: SK): List<Table> {
        val queryConditional = QueryConditional
            .sortGreaterThan(
                Key.builder()
                    .setPrimaryKeys(pk, sk)
                    .build(),
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun findAllByPKAndSKGreaterThanOrEqualTo(pk: PK, sk: SK): List<Table> {
        val queryConditional = QueryConditional
            .sortGreaterThanOrEqualTo(
                Key.builder()
                    .setPrimaryKeys(pk, sk)
                    .build(),
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun findAllByPKAndSKLessThan(pk: PK, sk: SK): List<Table> {
        val queryConditional = QueryConditional
            .sortLessThan(
                Key.builder()
                    .setPrimaryKeys(pk, sk)
                    .build(),
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun findAllByPKAndSKLessThanOrEqualTo(pk: PK, sk: SK): List<Table> {
        val queryConditional = QueryConditional
            .sortLessThanOrEqualTo(
                Key.builder()
                    .setPrimaryKeys(pk, sk)
                    .build(),
            )

        return dynamoDbTable
            .query(queryConditional)
            .toEntities()
    }

    override fun findByPrimaryKeys(pk: PK, sk: SK): Table? {
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

    override fun delete(item: Table) {
        dynamoDbTable.deleteItem(item)
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
