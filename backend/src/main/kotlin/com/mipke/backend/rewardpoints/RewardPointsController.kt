package com.mipke.backend.transactions

import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

class TransactionController(
        private val transactionService: TransactionService
) {

    @GetMapping("/transactions")
    fun getAllTransactions(): ResponseEntity<List<TransactionEntity>> =
            ResponseEntity.ok(transactionService.getAllTransactions())
}
