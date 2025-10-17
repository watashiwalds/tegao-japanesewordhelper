package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import timber.log.Timber

object MaziiResponseConverter {
    fun toDomainWordList(json: JsonObject): List<Word> {
        //TODO
        var words = mutableListOf<Word>()
        try {
            val rawList = json.getAsJsonObject("data").getAsJsonArray("words")
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
                    var meansT = mutableListOf<Word.Definition>()
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
    fun toDomainKanjiList(json: JsonObject): List<Kanji> {
        //TODO
        return listOf(Kanji(
            id = 0,
            character = "Mazii KANJI fetching success",
            meaning = "JSON size: ${json.size()}",
            strokeCount = 0,
            frequency = 0
        ))
    }
}