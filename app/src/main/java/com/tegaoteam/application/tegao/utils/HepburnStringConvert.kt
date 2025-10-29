package com.tegaoteam.application.tegao.utils

object HepburnStringConvert {
    private val hepburnTable = mapOf(
        "a" to "あ", "i" to "い", "u" to "う", "e" to "え", "o" to "お",
        "ka" to "か", "ki" to "き", "ku" to "く", "ke" to "け", "ko" to "こ",
        "sa" to "さ", "shi" to "し", "su" to "す", "se" to "せ", "so" to "そ",
        "ta" to "た", "chi" to "ち", "tsu" to "つ", "te" to "て", "to" to "と",
        "na" to "な", "ni" to "に", "nu" to "ぬ", "ne" to "ね", "no" to "の",
        "ha" to "は", "hi" to "ひ", "fu" to "ふ", "he" to "へ", "ho" to "ほ",
        "ma" to "ま", "mi" to "み", "mu" to "む", "me" to "め", "mo" to "も",
        "ya" to "や", "yu" to "ゆ", "yo" to "よ",
        "ra" to "ら", "ri" to "り", "ru" to "る", "re" to "れ", "ro" to "ろ",
        "wa" to "わ", "wo" to "を",
        "nn" to "ん", "n" to "ん",
        "ga" to "が", "gi" to "ぎ", "gu" to "ぐ", "ge" to "げ", "go" to "ご",
        "za" to "ざ", "ji" to "じ", "zu" to "ず", "ze" to "ぜ", "zo" to "ぞ",
        "da" to "だ", "de" to "で", "do" to "ど",
        "ba" to "ば", "bi" to "び", "bu" to "ぶ", "be" to "べ", "bo" to "ぼ",
        "pa" to "ぱ", "pi" to "ぴ", "pu" to "ぷ", "pe" to "ぺ", "po" to "ぽ",
        "kya" to "きゃ", "kyu" to "きゅ", "kyo" to "きょ",
        "sha" to "しゃ", "shu" to "しゅ", "sho" to "しょ",
        "cha" to "ちゃ", "chu" to "ちゅ", "cho" to "ちょ",
        "nya" to "にゃ", "nyu" to "にゅ", "nyo" to "にょ",
        "hya" to "ひゃ", "hyu" to "ひゅ", "hyo" to "ひょ",
        "mya" to "みゃ", "myu" to "みゅ", "myo" to "みょ",
        "rya" to "りゃ", "ryu" to "りゅ", "ryo" to "りょ",
        "gya" to "ぎゃ", "gyu" to "ぎゅ", "gyo" to "ぎょ",
        "ja" to "じゃ", "ju" to "じゅ", "jo" to "じょ",
        "bya" to "びゃ", "byu" to "びゅ", "byo" to "びょ",
        "pya" to "ぴゃ", "pyu" to "ぴゅ", "pyo" to "ぴょ",

        "si" to "し", "ti" to "ち", "tu" to "つ", "hu" to "ふ", "di" to "ぢ", "du" to "づ", "zi" to "じ",
        "zya" to "じゃ", "zyu" to "じゅ", "zyo" to "じょ",
        "dya" to "じゃ", "dyu" to "じゅ", "dyo" to "じょ",
        "short" to "っ"
    )
    private val stopWords = listOf('a', 'i', 'u', 'e', 'o')

    fun toHiragana(romaji: String): String {
        var hina = StringBuilder()
        var assembly = StringBuilder()

        for (char in romaji.lowercase()) {
            if (char in stopWords) {
                assembly.append(char)
                hina.append(hepburnTable.getOrDefault(assembly.toString(), assembly.toString()))
                assembly.clear()
            } else {
                if (char in 'a'..'z') {
                    if (assembly.lastOrNull() == 'n') {
                        hina.append(hepburnTable.getOrDefault(assembly.toString(), assembly.toString()))
                        assembly.clear()
                        if (char != 'n') assembly.append(char)
                    } else if (assembly.lastOrNull() == char){
                        hina.append(hepburnTable["short"])
                    } else {
                        assembly.append(char)
                    }
                } else {
                    if (assembly.isNotEmpty()) {
                        hina.append(hepburnTable.getOrDefault(assembly.toString(), assembly.toString()))
                        assembly.clear()
                    }
                    hina.append(char)
                }
            }
        }
        hina.append(hepburnTable.getOrDefault(assembly.toString(), assembly.toString()))

        return hina.toString()
    }
}