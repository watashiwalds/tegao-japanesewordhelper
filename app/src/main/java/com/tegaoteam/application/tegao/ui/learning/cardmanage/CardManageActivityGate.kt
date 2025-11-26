package com.tegaoteam.application.tegao.ui.learning.cardmanage

import android.content.Context
import android.content.Intent
import com.tegaoteam.application.tegao.R

object CardManageActivityGate {
    const val KEY_ACTIONID = "actionId"
    const val KEY_DATAID = "dataId"
    const val ACTION_GROUPLIST = 0
    const val ACTION_CARDLIST = 1
    const val ACTION_EDITGROUP = 2
    const val ACTION_EDITCARD = 3

    fun departIntent(context: Context, actionId: Int = ACTION_GROUPLIST, dataId: Long? = null): Intent {
        return Intent(context, CardManageActivity::class.java).apply {
            putExtra(KEY_ACTIONID, actionId)
            putExtra(KEY_DATAID, dataId)
        }
    }

    fun arriveIntentData(intent: Intent): Long {
        return intent.getLongExtra(KEY_DATAID, 0)
    }

    fun arriveIntentFrag(intent: Intent): Int {
        val res = intent.getIntExtra(KEY_ACTIONID, ACTION_GROUPLIST)
        return if (res !in listOf(ACTION_GROUPLIST, ACTION_CARDLIST, ACTION_EDITGROUP, ACTION_EDITCARD)) res else ACTION_GROUPLIST
    }
}