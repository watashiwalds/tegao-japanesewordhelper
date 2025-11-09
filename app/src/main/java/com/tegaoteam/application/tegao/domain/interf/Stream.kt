package com.tegaoteam.application.tegao.domain.interf

interface Stream<T> {
    suspend fun collect(collector: suspend (T) -> Unit)
}