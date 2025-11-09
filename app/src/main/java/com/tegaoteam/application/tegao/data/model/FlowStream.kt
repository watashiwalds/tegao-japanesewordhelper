package com.tegaoteam.application.tegao.data.model

import com.tegaoteam.application.tegao.domain.interf.Stream
import kotlinx.coroutines.flow.Flow

class FlowStream<T>(val flow: Flow<T>): Stream<T> {
    override suspend fun collect(collector: suspend (T) -> Unit) {
        flow.collect { collector(it) }
    }
}