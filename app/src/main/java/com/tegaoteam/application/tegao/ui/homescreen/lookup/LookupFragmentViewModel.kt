package com.tegaoteam.application.tegao.ui.homescreen.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tegaoteam.application.tegao.utils.EventBeacon

class LookupFragmentViewModel(app: Application): AndroidViewModel(app) {
    val evNavigateToLookupActivity = EventBeacon()
}