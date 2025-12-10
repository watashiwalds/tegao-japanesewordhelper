package com.tegaoteam.application.tegao.data.config

import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryNetworkApi
import com.tegaoteam.application.tegao.data.network.dictionaries.jisho.JishoDictionaryApi
import com.tegaoteam.application.tegao.data.network.dictionaries.jisho.JishoResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.mazii.MaziiDictionaryApi
import com.tegaoteam.application.tegao.data.network.dictionaries.mazii.MaziiResponseConverter
import com.tegaoteam.application.tegao.domain.model.Dictionary

object DictionaryConfig {
    const val DICT_ALL = 1
    const val DICT_WORD = 2
    const val DICT_KANJI = 3

    val SIMDICT_MAZII = Dictionary(
        MaziiDictionaryApi.DICTIONARY_ID,
        "Mazii",
        true,
        DICT_ALL
    )
    val SIMDICT_JISHO = Dictionary(
        JishoDictionaryApi.DICTIONARY_ID,
        "Jisho",
        true,
        DICT_WORD
    )

    fun getDictionariesList() = listOf(
        SIMDICT_MAZII,
        SIMDICT_JISHO
    )

    fun getDictionariesPack() = mapOf(
        MaziiDictionaryApi.api to MaziiResponseConverter(),
        JishoDictionaryApi.api to JishoResponseConverter()
    )
}