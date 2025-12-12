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
import com.tegaoteam.application.tegao.databinding.FragmentSettingBinding
import com.tegaoteam.application.tegao.ui.component.generics.listnavigation.ListNavigationListAdapter

class SettingListFragment : Fragment() {
    private lateinit var _viewModel: SettingListViewModel
    private lateinit var _binding: FragmentSettingBinding
    private lateinit var _listAdapter: ListNavigationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewModel = ViewModelProvider(this)[SettingListViewModel::class]

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting, container, false)

        val navController = findNavController()
        _listAdapter = ListNavigationListAdapter().apply {
            navigatingFunction = { actionId ->
                if (actionId != 0) navController.navigate(actionId)
            }
            submitList(_viewModel.settingNavigations)
        }
        _binding.loSettingListLst.adapter = _listAdapter

        return _binding.root
    }

}