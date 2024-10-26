package com.sjyt.springboot_dynamodb.repository.doubles

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.model.SecondaryIndex
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository


class DummyDynamoDBRepository<Table: TableEntity>: NoSQLRepository<Table> {
    override fun findAll(): List<Table> {
        return emptyList()
    }

    override fun <PK> findAllByPK(pk: PK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findAllByPKAndSKBetween(pk: PK, startSk: SK, endSk: SK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findAllByPKAndSKBeginsWith(pk: PK, beginningOfSk: SK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findAllByPKAndSKGreaterThan(pk: PK, sk: SK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findAllByPKAndSKGreaterThanOrEqualTo(pk: PK, sk: SK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findAllByPKAndSKLessThan(pk: PK, sk: SK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findAllByPKAndSKLessThanOrEqualTo(pk: PK, sk: SK): List<Table> {
        return emptyList()
    }

    override fun <PK, SK> findByPrimaryKeys(pk: PK, sk: SK): Table? {
        return null
    }

    override fun <GSIPK, GSISK> findAllByGSI(gsi: SecondaryIndex<GSIPK, GSISK>): List<Table> {
        return emptyList()
    }

    override fun <LSIPK, LSISK> findAllByLSI(lsi: SecondaryIndex<LSIPK, LSISK>): List<Table> {
        return emptyList()
    }

    override fun save(item: Table) {
    }
}