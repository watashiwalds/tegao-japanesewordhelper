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
                    val tagsT = mutableListOf(
                        Pair("source", "Mazii"),
                        Pair("lang", wObj.get("label").takeUnless { it.isJsonNull }?.asString)
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
                        val mTags = mObj.get("kind").takeUnless { i -> i != null && i.isJsonNull }?.asString?.split(", ")?.map { "kind" to it }?.toMutableList<Pair<String, String?>>()
                        val mXpds = mutableListOf<Pair<String, String>>()
                        if (mObj.has("examples") && !mObj.get("examples").isJsonNull) for (ex in mObj.getAsJsonArray("examples")) {
                            val exObj = ex.asJsonObject
                            mXpds.add(Pair("example", "" +
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

                    val idT = wObj.get("mobileId").takeUnless { it.isJsonNull }?.asInt
                    val readingT = wObj.get("word").takeUnless { it.isJsonNull }?.asString
                    val phoneticT = wObj.get("phonetic").takeUnless { it.isJsonNull }?.asString?.split(" ")

                    word = Word(
                        id = idT?: 0,
                        reading = readingT?: "",
                        furigana = phoneticT?: listOf(),
                        tags = tagsT,
                        definitions = meansT,
                    )
                }
                if (word != null) words.add(word)
            }
        } catch (e: Exception) {
            words.add(Word(
                id = -1,
                reading = "Converting error\n",
                furigana = listOf(e.toString()),
                definitions = listOf()
            ))
            Timber.e(e, "Error")
        }
        if (words.isEmpty()) words.add(Word(
            id = -1,
            reading = "",
            furigana = listOf("Không tìm thấy kết quả nào"),
            definitions = listOf()
        ))
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
                    val compsT = if (!kObj.get("compDetail").isJsonNull)
                        kObj.getAsJsonArray("compDetail").map {
                            it.asJsonObject.get("w").asString to it.asJsonObject.get("h").takeUnless { t -> t.isJsonNull }?.asString
                        }.toMutableList()
                    else mutableListOf()

                    val tagsT = mutableListOf<Pair<String, String?>>().apply {
                        if (kObj.has("freq")) add("frequency" to kObj.get("freq").takeUnless { it.isJsonNull }?.asString)
                        if (kObj.has("stroke_count")) add("stroke" to kObj.get("stroke_count").takeUnless { it.isJsonNull }?.asString)
                        if (kObj.has("level")) add("jlpt" to kObj.get("level").takeUnless { it.isJsonNull }?.asJsonArray?.joinToString(", ") { it.asString })
                    }

                    val additionalT = mutableListOf<Pair<String, String>>().apply {
                        if (kObj.has("detail")) add("detail" to kObj.get("detail").asString.replace("##", "\n"))
                        if (kObj.has("tips")) add("tips" to kObj.getAsJsonObject("tips").entrySet().map { it.value }.joinToString("\n"))
                    }

                    val idT = kObj.get("mobileId").takeUnless { it.isJsonNull }?.asInt
                    val charT = kObj.get("kanji").takeUnless { it.isJsonNull }?.asString
                    val kunyomiT = kObj.get("kun").takeUnless { it.isJsonNull }?.asString
                    val onyomiT = kObj.get("on").takeUnless { it.isJsonNull }?.asString
                    val meaningT = kObj.get("mean").takeUnless { it.isJsonNull }?.asString

                    kanji = Kanji(
                        id = idT?: 0,
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
            kanjis.add(Kanji(
                id = -1,
                character = "Converting error\n",
                meaning = e.toString()
            ))
            Timber.e(e, "Error")
        }
        if (kanjis.isEmpty()) kanjis.add(Kanji(
            id = 0,
            character = "",
            meaning = "Không có kết quả nào khớp"
        ))
        return kanjis
    }
}