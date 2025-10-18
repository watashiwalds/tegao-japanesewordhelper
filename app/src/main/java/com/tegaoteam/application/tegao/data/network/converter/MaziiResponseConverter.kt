package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import timber.log.Timber

class MaziiResponseConverter: DictionaryResponseConverter {
    override fun <T> toDomainWordList(rawData: T): List<Word> {
        val words = mutableListOf<Word>()
        if (rawData !is JsonObject) return words
        try {
            val rawList = rawData.getAsJsonObject("data").getAsJsonArray("words")
            for (w in rawList) {
                val wObj = w.asJsonObject
                var word: Word? = null
                if (wObj.has("type") && wObj.get("type").asString.equals("word")) {
                    //some default tags
                    val tagsT = mutableListOf<String>("mazii", wObj.get("label").asString)

                    //Mazii word's additionalInfo is all of it's possible pronunciations
                    val pronuns = wObj.getAsJsonArray("pronunciation").map { p ->
                        p.asJsonObject.getAsJsonArray("transcriptions").map { tr ->
                            tr.asJsonObject.get("romaji").asString
                        }
                    }
                    Timber.i("$pronuns")
                    val pConcat = ArrayDeque<String>()
                    for (p in pronuns) {
                        var pPopSize = pConcat.size
                        do {
                            val temp = if (pPopSize > 0) pConcat.removeFirst() else ""
                            for (tr in p) pConcat.add("$temp$tr")
                            pPopSize--
                        } while (pPopSize > 0)
                    }
                    val pronunsT = pConcat.joinToString("\n")

                    //convert definitions
                    val means = wObj.getAsJsonArray("means")
                    val meansT = mutableListOf<Word.Definition>()
                    for (m in means) {
                        val mObj = m.asJsonObject
                        val mTags = mObj.get("kind").asString.split(", ").toMutableList()
                        val mXpds = mutableListOf<Pair<String, String>>()
                        for (ex in mObj.getAsJsonArray("examples")) {
                            val exObj = ex.asJsonObject
                            mXpds.add(Pair("example", "${exObj.get("content")}\n${exObj.get("transcription")}\n${exObj.get("mean")}"))
                        }
                        val temp = Word.Definition(
                            tags = mTags,
                            meaning = mObj.get("mean").asString,
                            expandInfos = mXpds
                        )
                        meansT.add(temp)
                    }

                    word = Word(
                        id = wObj.get("mobileId").asInt,
                        reading = wObj.get("word").asString,
                        furigana = wObj.get("phonetic").asString,
                        tags = tagsT,
                        additionalInfo = pronunsT,
                        definitions = meansT,
                    )
                }
                if (word != null) words.add(word)
            }
        } catch (e: Exception) {
            words.add(Word(
                id = -1,
                reading = "Converting error\n",
                furigana = e.toString(),
                definitions = mutableListOf()
            ))
        }
        return words
    }

    override fun <T> toDomainKanjiList(rawData: T): List<Kanji> {
        //TODO
        val kanjis = mutableListOf<Kanji>()
        if (rawData !is JsonObject) return kanjis
        kanjis.add(Kanji(
            id = 0,
            character = "Mazii KANJI fetching success",
            meaning = "JSON size: ${rawData.size()}",
            strokeCount = 0,
            frequency = 0
        ))
        return kanjis
    }
}