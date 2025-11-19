package com.tegaoteam.application.tegao.ui.learning.cardcreate

import android.content.Context
import android.content.Intent
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardContentMaterial
import com.tegaoteam.application.tegao.ui.lookup.LookupActivity
import kotlinx.serialization.json.Json

object CardCreateActivityGate {
    const val KEY_CONTENTSOURCE = "contentSource"
    const val KEY_CONTENTTYPE = "contentType"
    const val TYPE_WORD = 0
    const val TYPE_KANJI = 1

    fun departIntent(context: Context, contentSource: Any): Intent {
        var extra: String? = null
        var type: Int? = null
        when (contentSource) {
            is Word -> {
                type = TYPE_WORD
                extra = Json.encodeToString(contentSource)
            }
            is Kanji -> {
                type = TYPE_KANJI
                extra = Json.encodeToString(contentSource)
            }
        }
        return Intent(context, CardCreateActivity::class.java).apply {
            if (extra != null) {
                putExtra(KEY_CONTENTSOURCE, extra)
                putExtra(KEY_CONTENTTYPE, type!!)
            }
        }
    }

    fun arriveIntent(intent: Intent): CardContentMaterial? {
        val extra = intent.getStringExtra(KEY_CONTENTSOURCE)
        if (extra == null) return null
        val type = intent.getIntExtra(KEY_CONTENTTYPE, -1)
        return when (type) {
            TYPE_WORD -> CardContentMaterial.fromWord(Json.decodeFromString(extra))
            TYPE_KANJI -> CardContentMaterial.fromKanji(Json.decodeFromString(extra))
            else -> null
        }
    }
}