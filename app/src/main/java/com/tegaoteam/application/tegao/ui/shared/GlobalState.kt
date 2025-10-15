package com.tegaoteam.application.tegao.ui.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

object GlobalState {
    enum class LookupMode { WORD, KANJI }
    private var _lookupMode = MutableStateFlow<LookupMode>(LookupMode.WORD)
    val lookupMode = _lookupMode.asStateFlow()
    fun setLookupMode(mode: LookupMode) { _lookupMode.value = mode; Timber.i("Mode change: ${lookupMode.value}") }
}