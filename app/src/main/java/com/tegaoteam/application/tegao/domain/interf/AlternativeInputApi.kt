package com.tegaoteam.application.tegao.domain.interf

interface AlternativeInputApi {
    suspend fun requestInputSuggestions(input: Any?): List<String>
}