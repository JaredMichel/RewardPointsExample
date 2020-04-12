package com.mipke.backend.transactions

import com.mipke.backend.transactions.model.NewTransactionPayload
import com.mipke.backend.transactions.model.TransactionEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class TransactionController(
        private val transactionService: TransactionService
) {

    @PostMapping("/transaction/{userId}")
    fun addNewTransaction(@PathVariable("userId") userId: String, @RequestBody payload: NewTransactionPayload): ResponseEntity<List<TransactionEntity>> {
        transactionService.addTransaction(userId, payload)
        return getTransactionsByUserId(userId);
    }

    @GetMapping("/transactions/{userId}")
    fun getTransactionsByUserId(@PathVariable("userId") userId: String): ResponseEntity<List<TransactionEntity>> =
            ResponseEntity.ok(transactionService.getTransactionsByUserId(userId))
}
