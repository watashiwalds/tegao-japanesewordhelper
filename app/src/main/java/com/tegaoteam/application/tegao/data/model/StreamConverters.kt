package com.tegaoteam.application.tegao.data.model

import com.tegaoteam.application.tegao.domain.independency.Stream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Stream<T>.asFlow(): Flow<T> = flow {
    collect { emit(it) }
}