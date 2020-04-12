package com.mipke.backend.rewardpoints

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RewardPointsController(
        private val rewardPointsService: RewardPointsService
) {

    @GetMapping("/reward-points")
    fun getAllTransactions(): ResponseEntity<Int> =
            ResponseEntity.ok(rewardPointsService.getCurrentRewardPointCount())
}
