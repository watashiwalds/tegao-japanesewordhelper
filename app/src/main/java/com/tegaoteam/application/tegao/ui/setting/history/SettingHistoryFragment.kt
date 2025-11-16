package com.tegaoteam.application.tegao.ui.setting.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SearchHistoryHub
import com.tegaoteam.application.tegao.databinding.FragmentSettingBinding
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.ui.setting.SettingEntryListAdapter
import com.tegaoteam.application.tegao.ui.setting.lookup.SettingLookupViewModel

class SettingHistoryFragment : Fragment() {
    private lateinit var _viewModel: SettingHistoryViewModel
    private lateinit var _binding: FragmentSettingBinding
    private lateinit var _searchHistoryRepo: SearchHistoryRepo
    private lateinit var _adapter: SettingEntryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _searchHistoryRepo = SearchHistoryHub()
        _viewModel = ViewModelProvider(this, SettingHistoryViewModel.Companion.ViewModelFactory(_searchHistoryRepo))[SettingHistoryViewModel::class]

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting, container, false)
        _adapter = SettingEntryListAdapter(viewLifecycleOwner).apply {
            submitList(_viewModel.historySettings)
        }
        _binding.loSettingListLst.adapter = _adapter
        _binding.executePendingBindings()

        return _binding.root
    }
}