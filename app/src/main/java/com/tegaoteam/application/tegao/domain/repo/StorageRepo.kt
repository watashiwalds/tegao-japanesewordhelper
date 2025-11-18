package com.tegaoteam.application.tegao.domain.repo

interface StorageRepo {
    suspend fun deleteSearchCaches(): Int
}