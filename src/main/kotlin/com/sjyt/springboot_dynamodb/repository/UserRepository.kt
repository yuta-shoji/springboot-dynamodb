package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

interface UserRepository {
    fun findAllUsers(): List<User>
    fun findUserByEmail(email: String): User?
    fun saveUser(user: User)
}

@Repository
class DefaultUserRepository(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Qualifier("mainTableEntity")
    private val dynamoDBRepository: DynamoDBRepository<MainTableEntity>
): UserRepository {
    override fun findAllUsers(): List<User> {
        return dynamoDBRepository
            .findAllByPK("USER")
            .toUsers()
    }

    override fun findUserByEmail(email: String): User? {
        return dynamoDBRepository
            .findByPrimaryKeys("USER", email)
            .toUserOrNull()
    }

    override fun saveUser(user: User) {
        dynamoDBRepository.save(user.toMainTableEntity())
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
