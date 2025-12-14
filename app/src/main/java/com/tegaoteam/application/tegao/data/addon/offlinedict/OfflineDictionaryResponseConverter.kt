package com.tegaoteam.application.tegao.data.addon.offlinedict

import com.google.gson.JsonParser
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.data.utils.ErrorResults
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import timber.log.Timber

class OfflineDictionaryResponseConverter: DictionaryResponseConverter {
    override fun <T> toDomainWordList(rawData: T): List<Word> {
        val words = mutableListOf<Word>()

        try {
            val pWords = JsonParser.parseString(rawData.toString()).asJsonArray
            pWords.forEach { word ->
                val pWord = word.asJsonObject

                val tId = pWord.get("id").asString
                val tReading = pWord.get("reading").asString
                val tFurigana = pWord.get("furigana").asJsonArray.map { it.asString }

                val tTags = mutableListOf<Word.Tag>()
                tTags.add(Word.Tag(
                    termKey = "source",
                    label = "Yomitan"
                ))
                tTags.addAll(pWord.get("tags").asJsonArray.map {
                    val pTag = it.asJsonObject
                    Word.Tag(
                        termKey = pTag.get("termKey").asString,
                        label = pTag.get("label").asString,
                        description = pTag.get("description")?.takeUnless { dsc -> dsc.isJsonNull }?.asString
                    )
                })

                val tDefs = pWord.get("definitions").asJsonArray.map {
                    val pDef = it.asJsonObject
                    Word.Definition(
                        meaning = pDef.get("meaning").asString
                    )
                }

                val tWord = Word(
                    id = tId,
                    reading = tReading,
                    furigana = tFurigana,
                    tags = tTags,
                    definitions = tDefs
                )
                words.add(tWord)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return words.apply { addAll(ErrorResults.DictionaryRes.wordRes(ErrorResults.DictionaryRes.PARSING_ERROR, e.message)) }
        }

        if (words.isEmpty()) return ErrorResults.DictionaryRes.wordRes(ErrorResults.DictionaryRes.EMPTY_RESULT)
        return words
    }

    override fun <T> toDomainKanjiList(rawData: T): List<Kanji> {
        val kanjis = mutableListOf<Kanji>()

        try {
            val pKanjis = JsonParser.parseString(rawData.toString()).asJsonArray
            pKanjis.forEach { kanji ->
                val pKanji = kanji.asJsonObject

                val tId = pKanji.get("id").asString
                val tChar = pKanji.get("character").asString
                val tMeaning = pKanji.get("meaning").asString
                val tKun = pKanji.get("kunyomi").asJsonArray.mapNotNull { it.asString.takeUnless { s -> s.isNullOrBlank() } }
                val tOn = pKanji.get("onyomi").asJsonArray.mapNotNull { it.asString.takeUnless { s -> s.isNullOrBlank() } }

                val tKanji = Kanji(
                    id = tId,
                    character = tChar,
                    kunyomi = tKun,
                    onyomi = tOn,
                    meaning = tMeaning
                )
                kanjis.add(tKanji)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return kanjis.apply { addAll(ErrorResults.DictionaryRes.kanjiRes(ErrorResults.DictionaryRes.EMPTY_RESULT, e.message)) }
        }

        if (kanjis.isEmpty()) return ErrorResults.DictionaryRes.kanjiRes(ErrorResults.DictionaryRes.EMPTY_RESULT)
        return kanjis
    }
}