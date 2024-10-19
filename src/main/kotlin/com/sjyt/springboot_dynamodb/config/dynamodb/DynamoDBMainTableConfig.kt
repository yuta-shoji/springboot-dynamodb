package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

@Configuration
class DynamoDBInformationTableConfig {
    @Bean
    fun dynamoDBRepositoryForMain(
        dynamoDbClient: DynamoDbEnhancedClient,
        @Value("\${spring.profiles.active}")
        environment: String,
    ): NoSQLRepository<MainTableEntity> {
        val dynamoDBTable = dynamoDbClient
            .table(
                "main_table_${environment}",
                TableSchema.fromBean(MainTableEntity::class.java),
            )
        return DynamoDBRepository(dynamoDBTable)
    }
}
