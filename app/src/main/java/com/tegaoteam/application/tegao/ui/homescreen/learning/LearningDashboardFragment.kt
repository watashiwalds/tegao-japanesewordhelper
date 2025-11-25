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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initVariables()
        initObservers()
        initView()

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main_learning_dashboard, container, false)
        return _binding.root
    }

    private fun initVariables() {
        _learningRepo = LearningHub()
        _viewModel = ViewModelProvider(requireActivity(), LearningDashboardFragmentViewModel.Companion.ViewModelFactory(_learningRepo))[LearningDashboardFragmentViewModel::class.java]
    }

    private fun initObservers() {
        _viewModel.apply {
            dashboardInfo.observe(viewLifecycleOwner) {
                _binding.dataInfo = it
                _binding.executePendingBindings()
            }
        }
    }

    private fun initView() {
        _viewModel.fetchDashboardInfo()
    }
}