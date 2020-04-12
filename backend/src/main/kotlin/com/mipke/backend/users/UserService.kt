package com.mipke.backend.users

import com.mipke.backend.common.ServiceCoordinatorConsumer
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService: ServiceCoordinatorConsumer() {

    private val userDataStore = mutableListOf(
            UserEntity(UUID.randomUUID().toString(), "Rick"),
            UserEntity(UUID.randomUUID().toString(), "James"),
            UserEntity(UUID.randomUUID().toString(), "Allison")
    )

    fun getUsers() = userDataStore
}
