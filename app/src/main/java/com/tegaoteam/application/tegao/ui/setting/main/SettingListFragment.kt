package com.tegaoteam.application.tegao.ui.setting.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentSettingListBinding

class SettingListFragment : Fragment() {
    private lateinit var _viewModel: SettingListViewModel
    private lateinit var _binding: FragmentSettingListBinding
    private lateinit var _listAdapter: SettingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewModel = ViewModelProvider(this)[SettingListViewModel::class]

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting_list, container, false)

        val navController = findNavController()
        _listAdapter = SettingListAdapter().apply {
            navigatingFunction = { actionId ->
                if (actionId != 0) navController.navigate(actionId)
            }
            submitList(_viewModel.settingNavigations)
        }
        _binding.loSettingListLst.adapter = _listAdapter

        return _binding.root
    }

}