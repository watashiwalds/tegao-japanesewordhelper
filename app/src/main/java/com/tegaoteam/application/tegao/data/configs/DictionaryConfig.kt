package com.tegaoteam.application.tegao.data.configs

import com.tegaoteam.application.tegao.domain.types.Dictionary

//TODO: Config placeholder, change when doing actual work
class DictionaryConfig {

    companion object {
        const val DICT_LOCAL = 0
        const val DICT_ONLINE = 1

        val SIMDICT_MAZII = Dictionary(
            0,
            "Mazii",
            DICT_ONLINE,
            """{
                |"url":"https://mazii.net/api/search",
                |"path_word":"/word",
                |"path_kanji":"/kanji",
                |"type_word":"api",
                |"payloadRequest_word":"{"dict":"javi","type":"word","query":"%s","limit":5,"page":1}",
                |"type_kanji":"api",
                |"payloadRequest_kanji":"{"dict":"javi","type":"kanji","query":"%s"}"
                |}""".trimMargin()
        )
        val SIMDICT_JISHO = Dictionary(
            1,
            "Jisho",
            DICT_ONLINE,
            """{
                |"url":"https://jisho.org",
                |"path_word":"/api/v1/search/words",
                |"type_word":"api",
                |"paramRequest_word":"?keyword=%s",
                |"path_kanji":"/search/",
                |"type_kanji":"web",
                |"paramRequest_kanji":"%s#kanji"
                |}""".trimMargin()
        )

        fun getDictionariesList() = listOf(
            SIMDICT_MAZII,
            SIMDICT_JISHO
        )
    }
}