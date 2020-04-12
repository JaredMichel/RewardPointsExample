package com.mipke.backend.rewardpoints

import com.mipke.backend.common.ServiceCoordinatorConsumer
import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.stereotype.Service

@Service
class RewardPointsService(): ServiceCoordinatorConsumer() {

    private var rewardPointCount: Int = 0

    fun getCurrentRewardPointCount(): Int = rewardPointCount

    fun processNewTransaction(transactionEntity: TransactionEntity) {
        rewardPointCount += transactionEntity.computeRewardPointForTransaction()
    }

    private fun TransactionEntity.computeRewardPointForTransaction(): Int {
        val newPoints: Double = when {
            this.amount >= 100.0 -> {
                50.0 + ((this.amount - 100.0) * 2.0)
            }
            this.amount > 50.0 -> {
                this.amount - 50.0
            }
            else -> {
                0.0
            }
        }
        return newPoints.toInt()
    }
}
