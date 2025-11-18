package com.tegaoteam.application.tegao.ui.setting.storage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.StorageHub
import com.tegaoteam.application.tegao.databinding.FragmentSettingBinding
import com.tegaoteam.application.tegao.domain.repo.StorageRepo
import com.tegaoteam.application.tegao.ui.setting.SettingEntryListAdapter

class SettingStorageFragment : Fragment() {
    private lateinit var _viewModel: SettingStorageViewModel
    private lateinit var _binding: FragmentSettingBinding
    private lateinit var _storageRepo: StorageRepo
    private lateinit var _adapter: SettingEntryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _storageRepo = StorageHub()
        _viewModel = ViewModelProvider(this, SettingStorageViewModel.Companion.ViewModelFactory(_storageRepo))[SettingStorageViewModel::class]

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting, container, false)
        _adapter = SettingEntryListAdapter(viewLifecycleOwner).apply {
            submitList(_viewModel.storageSettings)
        }
        _binding.loSettingListLst.adapter = _adapter
        _binding.executePendingBindings()

        return _binding.root
    }
}