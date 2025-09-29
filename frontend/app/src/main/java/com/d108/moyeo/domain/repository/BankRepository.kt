package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.Bank

interface BankRepository {
    suspend fun getAllBankList(): Result<List<Bank>>

}