package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.model.User
import org.springframework.stereotype.Repository

interface UserRepository {
    fun findAllUsers(): List<User>
    fun findUserByEmail(email: String): User?
}

@Repository
class DefaultUserRepository(
    private val dynamoDBRepository: NoSQLRepository<MainTableEntity>
): UserRepository {
    override fun findAllUsers(): List<User> {
        return dynamoDBRepository.findAllByPK("USER")
            .map {
                User(
                    name = it.userName,
                    email = it.sk,
                    age = it.age
                )
            }
    }

    override fun findUserByEmail(email: String): User? {
        val mainTableEntity = dynamoDBRepository
            .findByPKAndSK("USER", email) ?: return null

        return User(
            name = mainTableEntity.userName,
            email = mainTableEntity.sk,
            age = mainTableEntity.age
        )
    }
}
