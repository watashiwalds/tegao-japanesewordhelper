package com.tegaoteam.application.tegao.domain.interf

interface AlternativeInputApi {
    fun requestInputSuggestions(input: Any?): List<String>
}