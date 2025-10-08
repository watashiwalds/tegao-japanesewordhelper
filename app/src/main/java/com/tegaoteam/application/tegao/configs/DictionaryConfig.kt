package com.tegaoteam.application.tegao.configs

import org.json.JSONObject

//TODO: Config placeholder, change when doing actual work
class DictionaryConfig {
    data class Dict(
        val id: Int,
        val displayName: String,
        val type: Int,
        val additionalInfo: JSONObject = JSONObject()
    )

    companion object {
        const val DICT_LOCAL = 0
        const val DICT_ONLINE = 1

        fun getDictionariesList() = listOf(
            Dict(0, "Jitendex", DICT_LOCAL),
            Dict(1, "Mazii", DICT_ONLINE)
        )
    }
}