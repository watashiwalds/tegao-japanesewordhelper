package com.tegaoteam.application.tegao.data.network.dictionaries.mazii

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tegaoteam.application.tegao.data.utils.ErrorResults
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import timber.log.Timber
import kotlin.collections.toList

class MaziiResponseConverter: DictionaryResponseConverter {
    override fun <T> toDomainWordList(rawData: T): List<Word> {
        val words = mutableListOf<Word>()
        val rawJsonObj = JsonParser.parseString(rawData.toString()).asJsonObject
        if (rawJsonObj !is JsonObject) return ErrorResults.DictionaryRes.wordRes(ErrorResults.DictionaryRes.PARSING_ERROR)
        try {
            val rawList = rawJsonObj.getAsJsonObject("data").getAsJsonArray("words")
            for (w in rawList) {
                val wObj = w.asJsonObject
                var word: Word? = null
                if (wObj.has("type") && wObj.get("type").asString.equals("word")) {
                    //some default tags
                    val tagsT = mutableListOf(
                        Word.Tag("source", "Mazii"),
                        Word.Tag("lang", wObj.get("label").takeUnless { it.isJsonNull }?.asString?: "")
                    )

                    //  TODO: adapt to the change of pronunciation info of Mazii (not urgent)
                    //  Mazii changed the structure of pronunciation information
                    //  Now: [{
                    //      "kana": "<kana reading>"
                    //      "accent": "<pitch encode L(low) H(high) -(mid)>"
                    //  }, {}, ...]
                    //Mazii word's additionalInfo is all of it's possible pronunciations
//                    val pronuns = wObj.get("pronunciation").takeUnless { it.isJsonNull }?.asJsonArray?.map { p ->
//                        p.asJsonObject.get("transcriptions").takeUnless { it.isJsonNull }?.asJsonArray?.map { tr ->
//                            tr.asJsonObject.get("romaji").asString
//                        }
//                    }
//                    val pConcat = ArrayDeque<String>()
//                    if (pronuns != null) for (p in pronuns) {
//                        var pPopSize = pConcat.size
//                        do {
//                            val temp = if (pPopSize > 0) pConcat.removeFirst() else ""
//                            if (p != null) for (tr in p) pConcat.add("$temp$tr")
//                            pPopSize--
//                        } while (pPopSize > 0)
//                    }
//                    val pronunsT = mutableListOf(Pair("pronunciation", pConcat.joinToString("\n")))
//                    val pronunsT = listOf<Pair<String, String>>()

                    //convert definitions
                    val means = wObj.getAsJsonArray("means")
                    val meansT = mutableListOf<Word.Definition>()
                    if (!means.isJsonNull) for (m in means) {
                        val mObj = m.asJsonObject
                        val mTags = mObj.get("kind").takeUnless { i -> i != null && i.isJsonNull }
                            ?.asString
                            ?.split(", ")
                            ?.map { Word.Definition.Tag(it, it, MaziiTagValues.getTagDescription(it)) }
                            ?.toList()
                        val mXpds = mutableListOf<Word.Definition.ExpandInfo>()
                        if (mObj.has("examples") && !mObj.get("examples").isJsonNull) for (ex in mObj.getAsJsonArray("examples")) {
                            val exObj = ex.asJsonObject
                            mXpds.add(Word.Definition.ExpandInfo(
                                termKey = "example",
                                content = "" +
                                        "\uD83C\uDDFB\uD83C\uDDF3 ${exObj.get("mean").takeUnless { it.isJsonNull }?.asString}\n" +
                                        "\uD83D\uDD8B\uFE0F ${exObj.get("content").takeUnless { it.isJsonNull }?.asString}\n" +
                                        "\uD83D\uDDE3 ${exObj.get("transcription").takeUnless { it.isJsonNull }?.asString}"))

                        }
                        val temp = Word.Definition(
                            tags = mTags,
                            meaning = mObj.get("mean").takeUnless { it.isJsonNull }?.asString?: "",
                            expandInfos = mXpds
                        )
                        meansT.add(temp)
                    }

                    val idT = wObj.get("mobileId").takeUnless { it.isJsonNull }?.asString
                    val readingT = wObj.get("word").takeUnless { it.isJsonNull }?.asString
                    val phoneticT = wObj.get("phonetic").takeUnless { it.isJsonNull }?.asString?.split(" ")

                    word = Word(
                        id = idT?: "0",
                        reading = readingT?: "",
                        furigana = phoneticT?: listOf(),
                        tags = tagsT,
                        definitions = meansT,
                    )
                }
                if (word != null) words.add(word)
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
        val rawJsonObj = JsonParser.parseString(rawData.toString()).asJsonObject
        if (rawJsonObj !is JsonObject) return ErrorResults.DictionaryRes.kanjiRes(ErrorResults.DictionaryRes.PARSING_ERROR)
        try {
            val kanji = rawJsonObj.getAsJsonArray("results")
            for (k in kanji) {
                val kObj = k.asJsonObject
                var kanji: Kanji? = null
                if (kObj.has("kanji")) {
                    val compsT = if (!kObj.get("compDetail").isJsonNull)
                        kObj.getAsJsonArray("compDetail").map {
                            Kanji.Composite(it.asJsonObject.get("w").asString, it.asJsonObject.get("h").takeUnless { t -> t.isJsonNull }?.asString)
                        }
                    else listOf()

                    val tagsT = mutableListOf<Kanji.Tag>().apply {
                        if (kObj.has("freq")) add(Kanji.Tag("frequency", kObj.get("freq").takeUnless { it.isJsonNull }?.asString?: ""))
                        if (kObj.has("stroke_count")) add(Kanji.Tag("stroke", kObj.get("stroke_count").takeUnless { it.isJsonNull }?.asString?: ""))
                        if (kObj.has("level")) add(Kanji.Tag("jlpt", kObj.get("level").takeUnless { it.isJsonNull }?.asJsonArray?.joinToString(", ") { it.asString }?: ""))
                    }

                    val additionalT = mutableListOf<Kanji.AdditionalInfo>().apply {
                        if (kObj.has("detail")) add(Kanji.AdditionalInfo("detail", kObj.get("detail").asString.replace("##", "\n")))
                        if (kObj.has("tips")) add(Kanji.AdditionalInfo("tips", kObj.getAsJsonObject("tips").entrySet().map { it.value }.joinToString("\n")))
                    }

                    val idT = kObj.get("mobileId").takeUnless { it.isJsonNull }?.asString
                    val charT = kObj.get("kanji").takeUnless { it.isJsonNull }?.asString
                    val kunyomiT = kObj.get("kun").takeUnless { it.isJsonNull }?.asString?.split(' ')
                    val onyomiT = kObj.get("on").takeUnless { it.isJsonNull }?.asString?.split(' ')
                    val meaningT = kObj.get("mean").takeUnless { it.isJsonNull }?.asString

                    kanji = Kanji(
                        id = idT?: "0",
                        character = charT?: "",
                        kunyomi = kunyomiT,
                        onyomi = onyomiT,
                        composites = compsT,
                        meaning = meaningT?: "",
                        tags = tagsT,
                        additionalInfo = additionalT
                    )
                }
                if (kanji != null) kanjis.add(kanji)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return kanjis.apply { addAll(ErrorResults.DictionaryRes.kanjiRes(ErrorResults.DictionaryRes.EMPTY_RESULT, e.message)) }
        }

        if (kanjis.isEmpty()) return ErrorResults.DictionaryRes.kanjiRes(ErrorResults.DictionaryRes.EMPTY_RESULT)
        return kanjis
    }
}