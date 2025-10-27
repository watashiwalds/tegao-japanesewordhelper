package com.tegaoteam.application.tegao.ui.lookup

import android.content.Context
import android.content.Intent

object LookupActivityGate {
    const val KEY_KEYWORD = "keyword"

    fun departIntent(context: Context, keyword: String): Intent {
        return Intent(context, LookupActivity::class.java).apply {
            putExtra(KEY_KEYWORD, keyword)
        }
    }

    fun arriveIntent(intent: Intent): String? {
        return intent.getStringExtra(KEY_KEYWORD)
    }
}