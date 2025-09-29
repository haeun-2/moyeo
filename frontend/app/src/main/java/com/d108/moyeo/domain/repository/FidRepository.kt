package com.d108.moyeo.domain.repository

interface FidRepository {
    suspend fun getFid(): Result<String>
}