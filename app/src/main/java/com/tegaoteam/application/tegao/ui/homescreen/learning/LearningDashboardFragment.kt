package com.tegaoteam.application.tegao.ui.homescreen.learning

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.databinding.FragmentMainLearningDashboardBinding
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.homescreen.MainActivityViewModel
import com.tegaoteam.application.tegao.ui.learning.cardlearn.CardLearningActivityGate
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityGate
import com.tegaoteam.application.tegao.ui.learning.cardsharing.CardSharingActivity
import kotlin.getValue

class LearningDashboardFragment : Fragment() {
    private lateinit var _binding: FragmentMainLearningDashboardBinding
    private lateinit var _viewModel: LearningDashboardFragmentViewModel
    private val _parentViewModel: MainActivityViewModel by activityViewModels()
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

    override fun onResume() {
        _parentViewModel.fragmentChanged(R.id.main_learningDashboardFragment.toString())
        super.onResume()
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
            cardGroups.observe(viewLifecycleOwner) { groups ->
                _adapter.submitList(groups.map { group ->
                    val cardsType = _viewModel.fetchCardsTypeOfGroup(group.groupId)
                    val new = cardsType.map { it.count { type -> type.second == LearningDashboardFragmentViewModel.CARDSTATUS_NEW } }
                    val due = cardsType.map { it.count { type -> type.second == LearningDashboardFragmentViewModel.CARDSTATUS_DUE } }
                    val total = cardsType.map { it.size }
                    val progress = cardsType.map {
                        val total = it.size
                        val new = it.count{ type -> type.second == LearningDashboardFragmentViewModel.CARDSTATUS_NEW }
                        ((total.toDouble() - new)*100 / (if (total == 0) 1 else total)).toInt()
                    }
                    val clickLambda = { groupId: Long -> startActivity(CardLearningActivityGate.departIntent(requireContext(), groupId))}
                    LearningInfoDataClasses.DashboardCardGroupInfo(
                        groupEntry = group,
                        newCardsCount = new,
                        dueCardsCount = due,
                        allCardsCount = total,
                        clearProgress = progress,
                        onStartLearnClickListener = { clickLambda.invoke(group.groupId) },
                        onGroupClickListener = { clickLambda.invoke(group.groupId) },
                        lifecycleOwner = viewLifecycleOwner
                    )
                })
            }
        }
    }

    private fun initView() {
        _binding.cardGroupListRcy.adapter = _adapter

        _binding.cardGroupImportBtn.setOnClickListener {
            startActivity(Intent(context, CardSharingActivity::class.java))
        }
        _binding.cardGroupEditBtn.setOnClickListener {
            startActivity(CardManageActivityGate.departIntent(requireContext()))
        }
        _binding.learnAllBtn.setOnClickListener {
            startActivity(CardLearningActivityGate.departIntent(requireContext(), CardLearningActivityGate.GROUP_ALLGROUP))
        }
    }

    override fun onStart() {
        super.onStart()
        _viewModel.fetchDashboardInfo()
    }
}