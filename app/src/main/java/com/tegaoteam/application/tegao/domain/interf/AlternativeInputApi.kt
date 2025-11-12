package com.tegaoteam.application.tegao.domain.interf

interface AlternativeInputApi {
    fun requestInputSuggestions(input: Any?)
    fun registerCallback(callback: (List<String>) -> Unit)
}