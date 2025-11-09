package com.tegaoteam.application.tegao.domain.independency

interface Stream<T> {
    suspend fun collect(collector: suspend (T) -> Unit)
}