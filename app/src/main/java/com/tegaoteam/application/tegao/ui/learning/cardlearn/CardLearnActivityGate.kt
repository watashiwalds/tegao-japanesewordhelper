package com.tegaoteam.application.tegao.ui.learning.cardlearn

import android.content.Context
import android.content.Intent
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivity

object CardLearnActivityGate {
    const val KEY_GROUPID = "groupId"
    const val GROUP_ALLGROUP = 0L

    fun departIntent(context: Context, groupId: Long = GROUP_ALLGROUP): Intent {
        return Intent(context, CardLearningActivity::class.java).apply {
            putExtra(KEY_GROUPID, groupId)
        }
    }

    fun arriveGroupId(intent: Intent): Long {
        return intent.getLongExtra(KEY_GROUPID, 0)
    }
}