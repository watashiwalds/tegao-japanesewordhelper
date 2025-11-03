package com.tegaoteam.application.tegao.ui.setting.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ComponentListviewNavigationItemBinding
import com.tegaoteam.application.tegao.databinding.FragmentSettingListBinding
import com.tegaoteam.application.tegao.ui.component.generics.ListNavigationItemInfo

class SettingListFragment : Fragment() {
    private lateinit var _binding: FragmentSettingListBinding
    private lateinit var _listAdapter: SettingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_setting_list, container, false)

        _listAdapter = SettingListAdapter().apply { submitList(settingNavigations) }
        _binding.loSettingListLst.adapter = _listAdapter

        return _binding.root
    }

    private val settingNavigations = listOf(
        ListNavigationItemInfo(
            labelResId = R.string.setting_search_label,
            directionId = SettingListFragmentDirections.actionSettingListFragmentToSettingLookupFragment().actionId,
            detailResId = R.string.setting_search_detail,
            iconResId = R.drawable.ftc_round_search_128
        )
    )

}