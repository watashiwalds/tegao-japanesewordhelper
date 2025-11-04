package com.tegaoteam.application.tegao.ui.setting.lookup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SettingHub
import com.tegaoteam.application.tegao.databinding.FragmentSettingLookupBinding
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.setting.SettingEntryListAdapter

class SettingLookupFragment : Fragment() {
    private lateinit var _viewModel: SettingLookupViewModel
    private lateinit var _settingRepo: SettingRepo
    private lateinit var _binding: FragmentSettingLookupBinding

    private lateinit var _adapter: SettingEntryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _settingRepo = SettingHub()
        _viewModel = ViewModelProvider(this, SettingLookupViewModel.Companion.ViewModelFactory(_settingRepo))[SettingLookupViewModel::class]

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting_lookup, container, false)
        _adapter = SettingEntryListAdapter().apply {
            submitList(_viewModel.lookupSettings)
        }
        _binding.loSettingListLst.adapter = _adapter
        _binding.executePendingBindings()

        return _binding.root
    }
}