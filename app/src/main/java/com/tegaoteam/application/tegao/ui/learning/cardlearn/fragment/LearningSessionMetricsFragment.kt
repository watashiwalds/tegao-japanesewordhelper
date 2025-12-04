package com.tegaoteam.application.tegao.ui.learning.cardlearn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionMetricsBinding
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.ui.learning.cardlearn.CardLearningViewModel
import com.tegaoteam.application.tegao.ui.learning.cardlearn.adapter.ReviewHistoryListAdapter
import com.tegaoteam.application.tegao.ui.learning.cardlearn.model.ReviewHistoryInfo
import com.tegaoteam.application.tegao.utils.Time
import timber.log.Timber

class LearningSessionMetricsFragment: Fragment() {
    private lateinit var _binding: FragmentLearningSessionMetricsBinding
    private val _parentViewModel: CardLearningViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearningSessionMetricsBinding.inflate(layoutInflater, container, false)

        initView()
        initObservers()

        return _binding.root
    }

    private fun initView() {
        val rmbRv = _parentViewModel.rememberedReview
        val rptRv = _parentViewModel.repeatedReview
        val sumRv = rmbRv + rptRv
        val accuracyPercentage = (rmbRv * 100.0) / sumRv

        _binding.apply {
            totalReviewTxt.text = getString(R.string.card_learn_metrics_totalReview, sumRv)
            rememberedReviewTxt.text = rmbRv.toString()
            repeatedReviewTxt.text = rptRv.toString()
            loSessionProgressCpi.progress = accuracyPercentage.toInt()
            executePendingBindings()
        }

        val adapter = ReviewHistoryListAdapter()
        val histories = _parentViewModel.sessionLearnedRepeats.map {
            val entry = _parentViewModel.sessionCards.value?.find { card -> it.cardId == card.cardId }
            ReviewHistoryInfo(
                id = it.cardId,
                label = getString(R.string.card_manage_cardItem_label, it.cardId.toString()),
                quickInfo = entry?.front?: "",
                dueIn = Time.absoluteTimeDifferenceBetween(it.lastRepeat, it.nextRepeat, Time.DIFF_DAY),
                labelIcon = entry?.type.let { etype -> LearningCardConst.Type.entries.find { ctype -> ctype.id == etype }?.iconResId }
            )
        }
        _binding.sessionCardsHistoryRcy.apply {
            this.adapter = adapter
            adapter.submitList(histories)
        }

        _binding.finishSessionBtn.setOnClickListener {
            requireActivity().finish()
        }
        _binding.executePendingBindings()
    }

    private fun initObservers() {

    }
}