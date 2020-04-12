package com.mipke.backend.transactions.model

import java.time.LocalDate

data class RawTransaction(val date: LocalDate, val amount: Double)
data class TransactionEntity(val id: String, val date: LocalDate, val amount: Double)
