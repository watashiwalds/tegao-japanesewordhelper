package com.tegaoteam.application.tegao.data.network.dictionaries.jisho

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tegaoteam.application.tegao.data.network.ErrorResults
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import okhttp3.ResponseBody
import timber.log.Timber

class JishoResponseConverter: DictionaryResponseConverter {
    override fun <T> toDomainWordList(rawData: T): List<Word> {
        val words = mutableListOf<Word>()

        val rawJsonObj = JsonParser.parseString(rawData.toString()).asJsonObject
        if (rawJsonObj !is JsonObject) return ErrorResults.DictionaryRes.wordRes(ErrorResults.DictionaryRes.PARSING_ERROR)
        val pWords = rawJsonObj.get("data").asJsonArray.takeUnless { it.isJsonNull }
        if (pWords == null) return ErrorResults.DictionaryRes.wordRes(ErrorResults.DictionaryRes.PARSING_ERROR)

        try { pWords.forEach { word -> {
            val pWord = word.asJsonObject

            val tId = pWord.get("slug").takeUnless { it.isJsonNull }?.asString

            val xReadings = mutableMapOf<String, MutableList<String>>()
            pWord.get("japanese").takeUnless { it.isJsonNull }?.asJsonArray?.forEach { reading ->
                val pReading = reading.asJsonObject
                val w = pReading.get("word").asString
                val r = pReading.get("reading").asString
                xReadings.apply {
                    if (get(w) == null) set(w, mutableListOf())
                    get(w)!!.add(r)
                }
            }
            val tReading = xReadings.keys.first()
            val tFurigana = xReadings[tReading]
            val tAlternateReadings = Word.AdditionalInfo(
                "alternateReadings",
                xReadings.entries.filter { (key, _) ->
                    key != tReading
                }.joinToString(", ") { (key, value) ->
                    "$key (${value.joinToString("、")})"
                }
            )

            val tTags = mutableListOf<Word.Tag>().apply {
                add(Word.Tag("source", "Jisho"))
                add(Word.Tag("lang", "jp-en"))
                if (pWord.get("is_common").takeUnless { it.isJsonNull }?.asBoolean == true) add(Word.Tag("isCommon", "common"))
                pWord.get("tags").takeUnless { it.isJsonNull }?.asJsonArray?.forEach {
                    val tag = it.asString
                    if (tag.contains("wanikani")) add(Word.Tag("wanikani", tag.replace("wanikani", "Wani-")))
                    else add(Word.Tag(tag, tag))
                }
                pWord.get("jlpt").takeUnless { it.isJsonNull }?.asJsonArray?.forEach {
                    val nx = it.asString.split("-").lastOrNull()
                    nx?.let { add(Word.Tag("jlpt", "JLPT-$nx")) }
                }
            }

            val pDefs = pWord.get("senses").takeUnless { it.isJsonNull }?.asJsonArray
            val tDefs = mutableListOf<Word.Definition>()
            pDefs?.forEach { def ->
                val pDef = def.asJsonObject

                val dTags = pDef.get("parts_of_speech").takeUnless { it.isJsonNull }?.asJsonArray?.map {
                    val tag = it.asString
                    Word.Definition.Tag(tag.lowercase(), tag, tag)
                }

                val dMeaning = pDef.get("english_definitions").takeUnless { it.isJsonNull }?.asJsonArray?.joinToString("; ") { it.asString }

                val dXpands = mutableListOf<Word.Definition.ExpandInfo>().apply {
                    pDef.get("tags").takeUnless { it.isJsonNull }?.asJsonArray?.apply {
                        add(Word.Definition.ExpandInfo("definitionLabel", joinToString("\n") { it.asString }))
                    }
                    pDef.get("info").takeUnless { it.isJsonNull }?.asJsonArray?.apply {
                        add(Word.Definition.ExpandInfo("info", joinToString("\n") { it.asString }))
                    }
                    pDef.get("restrictions").takeUnless { it.isJsonNull }?.asJsonArray?.apply {
                        add(Word.Definition.ExpandInfo("restrictions", "Only apply to ${joinToString("; ") { it.asString }}"))
                    }

                    val antonyms = pDef.get("antonyms").takeUnless { it.isJsonNull }?.asJsonArray?.joinToString("; ") { it.asString }
                    val seeAlso = pDef.get("see_also").takeUnless { it.isJsonNull }?.asJsonArray?.joinToString("; ") { it.asString }
                    var related = ""
                    if (antonyms?.isNotBlank()?: false) related = "Antonyms: $antonyms"
                    if (seeAlso?.isNotBlank()?: false) related = "$related${if (antonyms?.isNotBlank()?:false) "\n" else ""}See also: $seeAlso"
                    if (related.isNotBlank()) add(Word.Definition.ExpandInfo("related", related))
                }

                val tDef = Word.Definition(
                    tags = dTags,
                    meaning = dMeaning?: "",
                    expandInfos = dXpands
                )
                tDefs.add(tDef)
            }

            val tWord = Word(
                id = tId?: "0",
                reading = tReading,
                furigana = tFurigana?: listOf(),
                tags = tTags,
                additionalInfo = listOf(tAlternateReadings),
                definitions = tDefs,
            )
            words.add(tWord)
        } } } catch (e: Exception) {
            Timber.e(e)
        }

        if (words.isEmpty()) return ErrorResults.DictionaryRes.wordRes(ErrorResults.DictionaryRes.EMPTY_RESULT)
        return words
    }
    override fun <T> toDomainKanjiList(rawData: T): List<Kanji> {
        //TODO
        val kanjis = mutableListOf<Kanji>()
        if (rawData !is ResponseBody) return kanjis
        kanjis.add(Kanji(
            id = "0",
            character = "",
            meaning = "Jisho KANJI fetching success, size: ${rawData.string().length}"
        ))
        return kanjis
    }
}