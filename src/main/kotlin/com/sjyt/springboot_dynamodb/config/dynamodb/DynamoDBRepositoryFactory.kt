package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema


@Component
class DynamoDBRepositoryFactory(
    @Value("\${spring.profiles.active}")
    val environment: String,
    val dynamoDbClient: DynamoDbEnhancedClient,
) {
    final inline fun <reified Table: TableEntity> build(): NoSQLRepository<Table> {
        val instance = Table::class.java.getDeclaredConstructor().newInstance()
        val dynamoDBTable = dynamoDbClient.table(
            "${instance.tableName}_$environment",
            TableSchema.fromBean(Table::class.java)
        )
        return DynamoDBRepository(dynamoDBTable)
    }
}
