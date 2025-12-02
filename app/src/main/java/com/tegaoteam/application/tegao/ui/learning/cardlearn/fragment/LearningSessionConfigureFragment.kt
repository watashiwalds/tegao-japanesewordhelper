package com.tegaoteam.application.tegao.ui.learning.cardlearn.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionConfigureBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.ui.learning.cardlearn.CardLearningActivityGate
import com.tegaoteam.application.tegao.ui.learning.cardlearn.CardLearningViewModel
import com.tegaoteam.application.tegao.ui.learning.cardlearn.model.LearnCardInfo
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.setEnableWithBackgroundCue

class LearningSessionConfigureFragment : Fragment() {
    private lateinit var _binding: FragmentLearningSessionConfigureBinding
    private val _parentViewModel: CardLearningViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearningSessionConfigureBinding.inflate(layoutInflater, container, false)

        initObservers()
        initView()

        return _binding.root
    }

    private fun initObservers() {
        _parentViewModel.apply {
            cardGroups.observe(viewLifecycleOwner) {
                fetchLearnableCards()
                _binding.learningCardGroupTxt.apply {
                    text = if (learnCardGroupId == CardLearningActivityGate.GROUP_ALLGROUP) {
                        String.format(getString(R.string.card_learn_config_groups_allGroups), it.size)
                    } else {
                        it.firstOrNull{ grp -> grp.groupId == learnCardGroupId }?.label
                    }
                }
            }
            learnableCardsStatus.observe(viewLifecycleOwner) { stats ->
                if (stats.isEmpty()) rejectLearningSession(REJECT_NOCARD)
                else {
                    val maxNew = stats.count { it.status == LearnCardInfo.STATUS_NEW }
                    val maxDue = stats.count { it.status == LearnCardInfo.STATUS_DUE }
                    restraintLearnCardsLimit(maxNew, maxDue)
                    markReadyToLearn()
                }
            }
        }
    }

    private fun initView() {
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            label = getString(R.string.card_learn_config_headerTitle),
            backOnClickListener = { requireActivity().finish() }
        )
        _binding.apply {
            startLearningBtn.setOnClickListener {
                submitConfigs()
                findNavController().navigate(R.id.learningSessionRunFragment, null, navOptions { popUpTo(R.id.learningSessionConfigureFragment) {inclusive = true} })
            }
            loNoRatingOptionCst.setOnClickListener { noRatingModeSwt.toggle() }
            executePendingBindings()
        }
    }

    //todo: implement learning config of preferred default maxNew + maxDue
    private fun restraintLearnCardsLimit(maxNew: Int, maxDue: Int) {
        _binding.apply {
            maxNewCardsBtn.setOnClickListener { newCardCapEdt.editableText.apply { clear(); append(maxNew.toString()) } }
            maxDueCardsBtn.setOnClickListener { dueCardCapEdt.editableText.apply { clear(); append(maxDue.toString()) } }
            newCardCapEdt.apply {
                doOnTextChanged { text, start, end, count ->
                    var lim = text.toString().toIntOrNull()?: 0
                    lim = if (lim < 0) 0 else if (lim > maxNew) maxNew else -1
                    if (lim != -1) editableText.apply { clear(); append(lim.toString()) }
                }
                editableText.append( if (maxNew > 5) "5" else "$maxNew" )
                isEnabled = true
            }
            dueCardCapEdt.apply {
                doOnTextChanged { text, start, end, count ->
                    var lim = text.toString().toIntOrNull()?: 0
                    lim = if (lim < 0) 0 else if (lim > maxDue) maxDue else -1
                    if (lim != -1) editableText.apply { clear(); append(lim.toString()) }
                }
                editableText.append( if (maxDue > 50) "50" else "$maxDue" )
                isEnabled = true
            }
            executePendingBindings()
        }
    }

    private fun submitConfigs() {
        _binding.apply {
            _parentViewModel.submitConfigs(
                newCardCapEdt.text.toString().toIntOrNull()?: 0,
                dueCardCapEdt.text.toString().toIntOrNull()?: 0,
                false)
        }
    }

    private fun markReadyToLearn() {
        _binding.startLearningBtn.setEnableWithBackgroundCue(true)
    }

    private val REJECT_NOCARD = 0
    private fun rejectLearningSession(reason: Int) {
        when (reason) {
            REJECT_NOCARD -> {
                AppToast.show(R.string.card_learn_terminate_noCardToLearn, AppToast.LENGTH_SHORT)
            }
        }
    }
}