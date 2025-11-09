package com.tegaoteam.application.tegao.domain.independency

sealed class RepoResult<out T> {
    data class Success<T>(val data: T): RepoResult<T>()
    data class Error<T>(val code: Int? = null, val message: String): RepoResult<Nothing>()
}