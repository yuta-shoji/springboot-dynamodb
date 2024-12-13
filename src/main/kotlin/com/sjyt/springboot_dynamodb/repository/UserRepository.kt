package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient

interface UserRepository {
    fun findAllUsers(): List<MainTableEntity>
    fun findUserByEmail(email: String): MainTableEntity?
    fun saveUser(user: User)
}

@Repository
class DefaultUserRepository(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    @Value("\${dynamodb.table-name-suffix}")
    tableNameSuffix: String,
) :
    UserRepository,
    DynamoDBRepository<MainTableEntity, String, String>(
        dynamoDbEnhancedClient,
        tableNameSuffix
    )
{

    override fun findAllUsers(): List<MainTableEntity> {
        return this
            .findAllByPK("USER")
    }

    override fun findUserByEmail(email: String): MainTableEntity? {
        return this
            .findByPrimaryKeys("USER", email)
    }

    override fun saveUser(user: User) {
        this.save(user.toMainTableEntity())
    }
}
