package com.tegaoteam.application.tegao.data.config

import com.tegaoteam.application.tegao.data.addon.offlinedict.OfflineDictionaryAddonConnection
import com.tegaoteam.application.tegao.data.addon.offlinedict.OfflineDictionaryResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.jisho.JishoDictionaryApi
import com.tegaoteam.application.tegao.data.network.dictionaries.jisho.JishoResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.mazii.MaziiDictionaryApi
import com.tegaoteam.application.tegao.data.network.dictionaries.mazii.MaziiResponseConverter
import com.tegaoteam.application.tegao.domain.interf.DictionaryLookupApi
import com.tegaoteam.application.tegao.domain.model.Dictionary

object DictionaryConfig {
    const val DICT_ALL = 1
    const val DICT_WORD = 2
    const val DICT_KANJI = 3

    private val SIMDICT_MAZII = Dictionary(
        MaziiDictionaryApi.DICTIONARY_ID,
        "Mazii",
        true,
        DICT_ALL
    )
    private val SIMDICT_JISHO = Dictionary(
        JishoDictionaryApi.DICTIONARY_ID,
        "Jisho",
        true,
        DICT_WORD
    )
    private val SIMDICT_YOMITAN = Dictionary(
        OfflineDictionaryAddonConnection.DICTIONARY_ID,
        "Yomitan",
        false,
        DICT_ALL
    )

    private val isOfflineYomitanAvailable = AddonConfig.isOfflineDictionaryAvailable

    private val dictList: List<Dictionary> by lazy {
        val initList = mutableListOf(
            SIMDICT_MAZII,
            SIMDICT_JISHO
        )
        if (isOfflineYomitanAvailable) initList.add(0, SIMDICT_YOMITAN)
        initList
    }

    private val dictPack: Map<DictionaryLookupApi, DictionaryResponseConverter> by lazy {
        val initMap = mutableMapOf(
            MaziiDictionaryApi.api to MaziiResponseConverter(),
            JishoDictionaryApi.api to JishoResponseConverter()
        )
        if (isOfflineYomitanAvailable) initMap[OfflineDictionaryAddonConnection.instance] = OfflineDictionaryResponseConverter()
        initMap
    }

    fun getDictionariesList() = dictList
    fun getDictionariesPack() = dictPack
}