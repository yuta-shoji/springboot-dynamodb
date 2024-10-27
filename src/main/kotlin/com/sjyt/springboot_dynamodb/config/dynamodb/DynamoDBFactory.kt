package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBEnhancedRepository
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLEnhancedRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface NoSQLFactory<Table: TableEntity> {
    fun buildDynamoDBRepository(tableClass: Class<Table>): NoSQLRepository<Table>
    fun buildDynamoDBEnhancedRepository(): NoSQLEnhancedRepository
}

@Component
class DynamoDBFactory<Table: TableEntity>(
    @Value("\${spring.profiles.active}")
    val environment: String,
    val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
): NoSQLFactory<Table> {
    override fun buildDynamoDBRepository(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        val instance = tableClass.getConstructor().newInstance()
        val dynamoDBTable = dynamoDbEnhancedClient.table(
            "${instance.tableName}_$environment",
            TableSchema.fromBean(tableClass)
        )
        return DynamoDBRepository(dynamoDBTable)
    }

    override fun buildDynamoDBEnhancedRepository(): NoSQLEnhancedRepository {
        return DynamoDBEnhancedRepository(environment, dynamoDbEnhancedClient)
    }
}
