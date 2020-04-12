package com.mipke.backend.transactions

import com.mipke.backend.common.ServiceCoordinatorConsumer
import com.mipke.backend.transactions.model.NewTransactionPayload
import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class TransactionService: ServiceCoordinatorConsumer() {

    private val transactionDataStore = ConcurrentHashMap<String, List<TransactionEntity>>()

    fun getTransactionsByUserId(userId: String) = transactionDataStore[userId] ?: listOf()

    fun addTransaction(userId: String, transactionPayload: NewTransactionPayload) {
        val newTransactionEntity = transactionPayload.toTransactionEntity()
        val currentTransactionsForUser = transactionDataStore[userId] ?: listOf()
        transactionDataStore[userId] = currentTransactionsForUser.plus(newTransactionEntity)
        serviceCoordinator.rewardPointsService.processNewTransaction(userId, newTransactionEntity)
    }

    /*
        Initialize some fake data once all beans are initialized
     */
    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent) {
        val initialTransactionData = serviceCoordinator.userService.getUsers().map { user ->
            /*
                Generate fake transactions for each of the last several months
             */
            val today = LocalDate.now()
            val transactionsForUser = (0..6).map { i ->
                val currentMonth = today.minusMonths(i.toLong())
                /*
                    Generate 10 transactions for each of the last several months
                 */
                (1..10).map {
                    val dayInMonth = currentMonth.genRandomDateInSameMonth()
                    TransactionEntity(genNewId(), dayInMonth, (50..250).random().toDouble())
                }.filter { tran -> tran.date.isBefore(today) || tran.date.isEqual(today) }
            }.flatten()
            user.id to transactionsForUser
        }

        initialTransactionData.forEach { userToTransactionPair ->
            userToTransactionPair.second.forEach { tran ->
                addTransaction(userToTransactionPair.first, NewTransactionPayload(tran.date, tran.amount))
            }
        }
    }

    companion object {
        private fun genNewId() = UUID.randomUUID().toString()

        private fun LocalDate.genRandomDateInSameMonth() =
                LocalDate.of(this.year, this.month, (1..28).random())

        private fun NewTransactionPayload.toTransactionEntity() =
                TransactionEntity(genNewId(), this.date, this.amount)
    }
}
