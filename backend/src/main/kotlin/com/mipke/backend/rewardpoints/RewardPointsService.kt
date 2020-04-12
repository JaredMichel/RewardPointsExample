package com.mipke.backend.transactions

import com.mipke.backend.transactions.model.Transaction
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TransactionService {

    private val transactionDataStore = mutableListOf(
            Transaction(LocalDate.now(), 10.0),
            Transaction(LocalDate.now(), 100.0),
            Transaction(LocalDate.now(), 80.0),
            Transaction(LocalDate.now(), 170.0)
    )

    fun getAllCurrentTransactions() = transactionDataStore

    fun addTransaction(transaction: Transaction) {
        transactionDataStore.add(transaction)
    }
}
