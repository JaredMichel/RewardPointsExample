package com.mipke.backend.transactions

import com.mipke.backend.common.ServiceCoordinatorConsumer
import com.mipke.backend.transactions.model.RawTransaction
import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class TransactionService: ServiceCoordinatorConsumer() {

    private val transactionDataStore = mutableListOf<TransactionEntity>()

    fun getAllTransactions() = transactionDataStore

    fun addTransaction(transaction: RawTransaction) {
        val newTransactionEntity = transaction.toDatastoreEntity()
        transactionDataStore.add(newTransactionEntity)
        serviceCoordinator.rewardPointsService.processNewTransaction(newTransactionEntity)
    }

    /*
        Initialize some fake data once all beans are initialized
     */
    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent) {
        listOf(
                RawTransaction(LocalDate.now(), 10.0),
                RawTransaction(LocalDate.now(), 100.0),
                RawTransaction(LocalDate.now(), 80.0),
                RawTransaction(LocalDate.now(), 170.0)
        )
                .forEach { trans -> addTransaction(trans) }
    }

    companion object {
        private fun genNewId() = UUID.randomUUID().toString()

        private fun RawTransaction.toDatastoreEntity() =
                TransactionEntity(genNewId(), this.date, this.amount)
    }
}
