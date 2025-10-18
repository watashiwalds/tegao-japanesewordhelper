package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

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
                    val tagsT = mutableListOf(
                        Pair("source", "mazii"),
                        Pair("lang", wObj.get("label").asString)
                    )

                    //Mazii word's additionalInfo is all of it's possible pronunciations
                    val pronuns = wObj.getAsJsonArray("pronunciation").map { p ->
                        p.asJsonObject.getAsJsonArray("transcriptions").map { tr ->
                            tr.asJsonObject.get("romaji").asString
                        }
                    }
                    val pConcat = ArrayDeque<String>()
                    for (p in pronuns) {
                        var pPopSize = pConcat.size
                        do {
                            val temp = if (pPopSize > 0) pConcat.removeFirst() else ""
                            for (tr in p) pConcat.add("$temp$tr")
                            pPopSize--
                        } while (pPopSize > 0)
                    }
                    val pronunsT = mutableListOf(Pair("pronunciation", pConcat.joinToString("\n")))

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
        val kanjis = mutableListOf<Kanji>()
        if (rawData !is JsonObject) return kanjis
        try {
            val kanji = rawData.getAsJsonArray("results")
            for (k in kanji) {
                val kObj = k.asJsonObject
                var kanji: Kanji? = null
                if (kObj.has("kanji")) {
                    val compsT: MutableList<Pair<String, String?>>? = kObj.getAsJsonArray("compDetail").map {
                        Pair<String, String?>(
                            it.asJsonObject.get("w").asString,
                            it.asJsonObject.get("h").asString)
                    }.toMutableList()

                    val detailsT = kObj.get("detail").asString.replace("##", "\n")

                    val tagsT = mutableListOf<Pair<String, String>>().apply {
                        when {
                            kObj.has("freq") -> add("frequency" to kObj.get("freq").asString)
                            kObj.has("stroke_count") -> add("stroke" to kObj.get("stroke_count").asString)
                            kObj.has("jlpt") -> add("jlpt" to kObj.getAsJsonArray("level").joinToString(", "))
                        }
                    }

                    val tipsT = mutableListOf<Pair<String, String>>().apply {
                        when {
                            kObj.has("tips") -> add("tips" to kObj.getAsJsonObject("tips").entrySet().map { it.value }.joinToString("\n"))
                        }
                    }

                    kanji = Kanji(
                        id = kObj.get("mobileId").asInt,
                        character = kObj.get("kanji").asString,
                        kunyomi = kObj.get("kun").asString,
                        onyomi = kObj.get("on").asString,
                        composites = compsT,
                        meaning = kObj.get("mean").asString,
                        details = detailsT,
                        tags = tagsT,
                        additionalInfo = tipsT
                    )
                }
                if (kanji != null) kanjis.add(kanji)
            }
        } catch (e: Exception) {
            kanjis.add(Kanji(
                id = -1,
                character = "Converting error\n",
                meaning = e.toString()
            ))
        }
        return kanjis
    }
}