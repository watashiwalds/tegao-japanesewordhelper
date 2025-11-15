package com.tegaoteam.application.tegao.ui.setting.addon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.data.hub.SettingHub
import com.tegaoteam.application.tegao.databinding.FragmentSettingAddonBinding
import com.tegaoteam.application.tegao.domain.repo.AddonRepo
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.setting.SettingEntryListAdapter

class SettingAddonFragment : Fragment() {
    private lateinit var _binding: FragmentSettingAddonBinding
    private lateinit var _viewModel: SettingAddonViewModel
    private lateinit var _adapter: SettingEntryListAdapter
    private lateinit var _settingRepo: SettingRepo
    private lateinit var _addonRepo: AddonRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _settingRepo = SettingHub()
        _addonRepo = AddonHub()
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting_addon, container, false)
        _viewModel = ViewModelProvider(this, SettingAddonViewModel.Companion.ViewModelFactory(_settingRepo, _addonRepo))[SettingAddonViewModel::class.java]

        _adapter = SettingEntryListAdapter(viewLifecycleOwner).apply {
            submitList(_viewModel.addonSettings)
        }
        _binding.loSettingListLst.adapter = _adapter
        _binding.executePendingBindings()
        return _binding.root
    }
}