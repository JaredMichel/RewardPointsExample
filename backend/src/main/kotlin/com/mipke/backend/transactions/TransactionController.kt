package com.mipke.backend.transactions

import com.mipke.backend.transactions.model.RawTransaction
import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController(
        private val transactionService: TransactionService
) {

    @PostMapping("/transaction")
    fun addNewTransaction(@RequestBody rawTransaction: RawTransaction): ResponseEntity<List<TransactionEntity>> {
        transactionService.addTransaction(rawTransaction)
        return getAllTransactions();
    }

    @GetMapping("/transactions")
    fun getAllTransactions(): ResponseEntity<List<TransactionEntity>> =
            ResponseEntity.ok(transactionService.getAllTransactions())
}
