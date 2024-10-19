package com.sjyt.springboot_dynamodb.service

import com.sjyt.springboot_dynamodb.model.User
import com.sjyt.springboot_dynamodb.repository.UserRepository
import org.springframework.stereotype.Service

interface UserService {
    fun findAllUsers(): List<User>
    fun findUserByEmail(email: String): User?
}

@Service
class DefaultUserService(
    private val userRepository: UserRepository
): UserService {
    override fun findAllUsers(): List<User> {
        // Implement some business logic here
        return userRepository.findAllUsers()
    }

    override fun findUserByEmail(email: String): User? {
        // Implement some business logic here
        return userRepository.findUserByEmail(email)
    }
}