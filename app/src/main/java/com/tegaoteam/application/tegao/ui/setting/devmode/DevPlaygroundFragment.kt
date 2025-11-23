package com.tegaoteam.application.tegao.ui.setting.devmode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentDevPlaygroundBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import timber.log.Timber

class DevPlaygroundFragment : Fragment() {
    private lateinit var _binding: FragmentDevPlaygroundBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dev_playground, container, false)
        viewModel = ViewModelProvider(this)[DevPlaygroundViewModel::class.java]

        initObservers()

        // Inflate the layout for this fragment
        return _binding.root
    }

    private lateinit var viewModel: DevPlaygroundViewModel
    private val cardsOfGroups = mutableMapOf<Long, List<CardEntry>>()
    private fun initObservers() {
        viewModel.evMakePrint.beacon.observe(viewLifecycleOwner) {
            if (viewModel.evMakePrint.receive()) {
                _binding.textView.text = cardsOfGroups.toString()
            }
        }
        viewModel.queriedCard.observe(viewLifecycleOwner) {
            val groupId = it.firstOrNull()?.groupId?: -1
            cardsOfGroups[groupId] = it
            Timber.i("1 batch fin")
            viewModel.evMakePrint.ignite()
        }
        viewModel.groups.observe(viewLifecycleOwner) {
            it.forEach { group -> viewModel.queryCardsByGroupId(group.groupId) }
            Timber.i("All query request sent")
        }
    }
}