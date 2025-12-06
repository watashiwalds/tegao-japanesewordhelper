package com.tegaoteam.application.tegao.ui.shared

import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.config.datastore.DefaultConfigs
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.data.hub.SettingHub
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.repo.AddonRepo
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

object FetchedConfigs {
    private val scope = TegaoApplication.appIOScope

    private val addonRepo: AddonRepo = AddonHub()
    private val settingRepo: SettingRepo = SettingHub()

    val isHepburnConverterEnabled: StateFlow<Boolean> = settingRepo.isHepburnConverterEnable().asFlow()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = DefaultConfigs.USE_HEPBURN_CONVERTER
        )
    val isHandwritingEnabled: StateFlow<Boolean> = settingRepo.isHandwritingAddonEnable().asFlow()
        .map { enabled -> addonRepo.isHandwritingAvailable() && enabled }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = DefaultConfigs.ENABLE_HANDWRITING_ADDON
        )
}