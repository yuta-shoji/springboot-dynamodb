package com.sjyt.springboot_dynamodb.controller

import com.sjyt.springboot_dynamodb.model.User
import com.sjyt.springboot_dynamodb.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun getAllUsers(): List<User> {
        return userService.findAllUsers()
    }

    @GetMapping("/{email}")
    fun getUserByEmail(@PathVariable email: String): User {
        return userService.findUserByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveUser(@RequestBody user: User) {
        userService.saveUser(user)
    }
}
