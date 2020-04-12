package com.mipke.backend.users

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
        private val userService: UserService
) {

    @GetMapping("/users")
    fun getAllTransactions(): ResponseEntity<List<UserEntity>> =
            ResponseEntity.ok(userService.getUsers())
}
