package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface NoSQLRepositoryFactory {
    fun <Table : TableEntity> create(entityClass: Class<Table>): NoSQLRepository<Table>
}

@Component
class DynamoDBRepositoryFactory(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    @Value("\${dynamodb.table-name-suffix}")
    private val tableNameSuffix: String,
): NoSQLRepositoryFactory {
    override fun <T : TableEntity> create(entityClass: Class<T>): NoSQLRepository<T> {
        val tableName = "${entityClass.getDeclaredConstructor().newInstance().tableName}_$tableNameSuffix"
        val table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromClass(entityClass))
        return DynamoDBRepository(table)
    }
}