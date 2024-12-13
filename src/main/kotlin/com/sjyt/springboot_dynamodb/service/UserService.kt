package com.sjyt.springboot_dynamodb.service

import com.sjyt.springboot_dynamodb.entity.toUser
import com.sjyt.springboot_dynamodb.entity.toUsers
import com.sjyt.springboot_dynamodb.model.User
import com.sjyt.springboot_dynamodb.repository.UserRepository
import org.springframework.stereotype.Service

interface UserService {
    fun findAllUsers(): List<User>
    fun findUserByEmail(email: String): User?
    fun saveUser(user: User)
}

@Service
class DefaultUserService(
    private val userRepository: UserRepository
): UserService {
    override fun findAllUsers(): List<User> {
        // Implement some business logic here
        return userRepository.findAllUsers().toUsers()
    }

    override fun findUserByEmail(email: String): User? {
        // Implement some business logic here
        return userRepository.findUserByEmail(email)?.toUser()
    }

    override fun saveUser(user: User) {
        userRepository.saveUser(user)
    }
}