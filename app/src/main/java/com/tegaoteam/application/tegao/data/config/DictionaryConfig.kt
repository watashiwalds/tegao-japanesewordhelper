package com.tegaoteam.application.tegao.data.config

import com.tegaoteam.application.tegao.domain.interf.DictionaryNetworkApi
import com.tegaoteam.application.tegao.data.network.api.JishoDictionaryApi
import com.tegaoteam.application.tegao.data.network.api.MaziiDictionaryApi
import com.tegaoteam.application.tegao.domain.model.Dictionary

//TODO: Config placeholder, change when doing actual work
class DictionaryConfig {

    companion object {
        const val DICT_LOCAL = 0
        const val DICT_ONLINE = 1

        val SIMDICT_MAZII = Dictionary(
            "mazii",
            "Mazii",
            DICT_ONLINE,
            """{
                "url":"https://mazii.net/api/search/",
                "path_word":"word",
                "path_kanji":"kanji",
                "type_word":"api",
                "payloadRequest_word":{
                    "dict": "javi",
                    "type": "word",
                    "query": "%s",
                    "limit": 5,
                    "page": 1
                },
                "type_kanji":"api",
                "payloadRequest_kanji": {
                    "dict": "javi",
                    "type": "kanji",
                    "query": "%s"
                }
                }""".trimIndent()
        )
        val SIMDICT_JISHO = Dictionary(
            "jisho",
            "Jisho",
            DICT_ONLINE,
            """{
                "url":"https://jisho.org/",
                "path_word":"api/v1/search/words",
                "type_word":"api",
                "paramRequest_word":{
                    "keyword":"%s"
                },
                "path_kanji":"search/",
                "type_kanji":"web",
                "pathAppend_kanji":"%s#kanji"
                }""".trimIndent()
        )

        val supportedSource = listOf<DictionaryNetworkApi>(MaziiDictionaryApi.api, JishoDictionaryApi.api)

        fun getDictionariesList() = listOf(
            SIMDICT_MAZII,
            SIMDICT_JISHO
        )

        fun getDictionariesApi() = supportedSource
    }
}