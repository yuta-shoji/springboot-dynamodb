package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface NoSQLRepositoryFactory<Table: TableEntity> {
    fun build(tableClass: Class<Table>): NoSQLRepository<Table>
}

@Component
class DynamoDBRepositoryFactory<Table: TableEntity>(
    @Value("\${spring.profiles.active}")
    val environment: String,
    val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
): NoSQLRepositoryFactory<Table> {
    override fun build(
        tableClass: Class<Table>
    ): NoSQLRepository<Table> {
        val instance = tableClass.getConstructor().newInstance()
        val dynamoDBTable = dynamoDbEnhancedClient.table(
            "${instance.tableName}_$environment",
            TableSchema.fromBean(tableClass)
        )
        return DynamoDBRepository(dynamoDBTable, dynamoDbEnhancedClient)
    }
}
