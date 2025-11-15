package com.tegaoteam.application.tegao.ui.setting.addon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SettingHub

class SettingAddonFragment : Fragment() {
    private lateinit var _viewModel: SettingAddonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewModel = ViewModelProvider(this, SettingAddonViewModel.Companion.ViewModelFactory(SettingHub()))[SettingAddonViewModel::class.java]
        return inflater.inflate(R.layout.fragment_setting_addon, container, false)
    }
}