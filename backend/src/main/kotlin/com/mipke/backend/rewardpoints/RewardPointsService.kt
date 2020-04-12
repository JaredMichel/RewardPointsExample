package com.mipke.backend.rewardpoints

import com.mipke.backend.common.ServiceCoordinatorConsumer
import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class RewardPointsService(): ServiceCoordinatorConsumer() {

    private val rewardPointCountMap = ConcurrentHashMap<String, Int>()

    fun getCurrentRewardPointCountByUserId(userId: String): Int = rewardPointCountMap[userId] ?: 0

    fun processNewTransaction(userId: String, transactionEntity: TransactionEntity) {
        val currentPointsForUser = rewardPointCountMap[userId] ?: 0
        rewardPointCountMap[userId] = currentPointsForUser + transactionEntity.computeRewardPointForTransaction()
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
