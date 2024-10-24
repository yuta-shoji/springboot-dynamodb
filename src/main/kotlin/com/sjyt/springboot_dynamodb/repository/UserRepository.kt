package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.config.dynamodb.DynamoDBRepositoryFactory
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.User
import org.springframework.stereotype.Repository

interface UserRepository: BaseRepository {
    fun findAllUsers(): List<User>
    fun findUserByEmail(email: String): User?
}

@Repository
class DefaultUserRepository(
    dynamoDBRepositoryFactory: DynamoDBRepositoryFactory
): UserRepository {
    override val dynamoDBRepository = dynamoDBRepositoryFactory
        .build<MainTableEntity>()

    override fun findAllUsers(): List<User> {
        return dynamoDBRepository
            .findAllByPK("USER")
            .toUsers()
    }

    override fun findUserByEmail(email: String): User? {
        return dynamoDBRepository
            .findByPartitionKeys("USER", email)
            .toUserOrNull()
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
