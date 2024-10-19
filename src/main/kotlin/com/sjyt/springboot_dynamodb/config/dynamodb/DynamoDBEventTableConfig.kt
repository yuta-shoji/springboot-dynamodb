package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

@Configuration
class DynamoDBEventTableConfig {
    @Bean
    fun dynamoDBRepositoryForEvent(
        dynamoDbClient: DynamoDbEnhancedClient,
        @Value("\${spring.profiles.active}")
        environment: String,
    ): NoSQLRepository<EventTableEntity> {
        val dynamoDBTable = dynamoDbClient
            .table(
                "event_table_${environment}",
                TableSchema.fromBean(EventTableEntity::class.java),
            )
        return DynamoDBRepository(dynamoDBTable)
    }
}
