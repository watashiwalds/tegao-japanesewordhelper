package com.tegaoteam.application.tegao.ui.homescreen.learning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.databinding.FragmentMainLearningDashboardBinding
import com.tegaoteam.application.tegao.domain.repo.LearningRepo

class LearningDashboardFragment : Fragment() {
    private lateinit var _binding: FragmentMainLearningDashboardBinding
    private lateinit var _viewModel: LearningDashboardFragmentViewModel
    private lateinit var _learningRepo: LearningRepo
    private lateinit var _adapter: DashboardGroupListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main_learning_dashboard, container, false)

        initVariables()
        initObservers()
        initView()

        return _binding.root
    }

    private fun initVariables() {
        _learningRepo = LearningHub()
        _viewModel = ViewModelProvider(requireActivity(), LearningDashboardFragmentViewModel.Companion.ViewModelFactory(_learningRepo))[LearningDashboardFragmentViewModel::class.java]
        _adapter = DashboardGroupListAdapter()
    }

    private fun initObservers() {
        _viewModel.apply {
            dashboardInfo.observe(viewLifecycleOwner) {
                _binding.dataInfo = it
                _binding.executePendingBindings()
            }
            dashboardGroups.observe(viewLifecycleOwner) {
                //todo: binding listener to the list
                it.forEach { grp -> grp.onStartLearnClickListener = {} }
                _adapter.submitList(it)
            }

            eventRepeatsByGroupUpdated.beacon.observe(viewLifecycleOwner) {
                if (eventRepeatsByGroupUpdated.receive()) composeDashboardGroup()
            }
        }
    }

    private fun initView() {
        _binding.cardGroupListRcy.adapter = _adapter
    }

    override fun onStart() {
        super.onStart()
        _viewModel.refreshData()
    }
}