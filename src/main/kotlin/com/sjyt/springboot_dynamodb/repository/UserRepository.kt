package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient

interface UserRepository {
    fun findAllUsers(): List<User>
    fun findUserByEmail(email: String): User?
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

    override fun findAllUsers(): List<User> {
        return this
            .findAllByPK("USER")
            .toUsers()
    }

    override fun findUserByEmail(email: String): User? {
        return this
            .findByPrimaryKeys("USER", email)
            .toUserOrNull()
    }

    override fun saveUser(user: User) {
        this.save(user.toMainTableEntity())
    }

    private fun MainTableEntity?.toUserOrNull(): User? {
        this ?: return null
        return User(
            name = this.userName ?: "",
            email = this.sk,
            age = this.age ?: 0
        )
    }

    private fun List<MainTableEntity>.toUsers(): List<User> {
        return this.map {
            User(
                name = it.userName ?: "",
                email = it.sk,
                age = it.age ?: 0
            )
        }
    }
}
