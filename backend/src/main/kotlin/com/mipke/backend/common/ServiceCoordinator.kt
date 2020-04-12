package com.mipke.backend.common

import com.mipke.backend.rewardpoints.RewardPointsService
import com.mipke.backend.transactions.TransactionService
import org.springframework.stereotype.Component

@Component
class ServiceCoordinator(
        val rewardPointsService: RewardPointsService,
        val transactionService: TransactionService
)
