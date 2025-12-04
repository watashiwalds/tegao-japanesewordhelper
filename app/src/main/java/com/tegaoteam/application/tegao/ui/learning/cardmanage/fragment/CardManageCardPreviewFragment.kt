package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.databinding.FragmentCardManagePreviewBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.component.learningcard.LearningCardBindingHelper
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.utils.setEnableWithBackgroundCue

class CardManageCardPreviewFragment: Fragment() {
    private lateinit var _binding: FragmentCardManagePreviewBinding
    private val _parentViewModel: CardManageActivityViewModel by activityViewModels()
    private var _cardId: Long? = null
    private lateinit var _cardEntry: CardEntry

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arriveArgs()

        _binding = FragmentCardManagePreviewBinding.inflate(layoutInflater, container, false)

        initObservers()

        return _binding.root
    }

    private fun arriveArgs() {
        _cardId = CardManageCardPreviewFragmentArgs.fromBundle(requireArguments()).cardId
    }

    private fun initObservers() {
        _parentViewModel.getCardById(_cardId?: 0).observe(viewLifecycleOwner) {
            if (it.cardId != _cardId) requireActivity().onBackPressedDispatcher.onBackPressed() else {
                _cardEntry = it
                initView()
            }
        }
    }

    private lateinit var _learningCardBindingHelper: LearningCardBindingHelper
    private fun initView() {
        if (!::_learningCardBindingHelper.isInitialized) _learningCardBindingHelper = LearningCardBindingHelper(requireActivity() as AppCompatActivity, viewLifecycleOwner, _cardEntry, _binding.viewLearningCard)
        _learningCardBindingHelper.apply {
            bindOnMode(LearningCardBindingHelper.MODE_NO_RATING)
            LearningCardBindingHelper.apply {
                setOnFrontFinalCollideListener(COLLIDE_NORTH, COLLIDE_WEST, COLLIDE_EAST, COLLIDE_SOUTH) {
                    _binding.flickCardBtn.setEnableWithBackgroundCue(false)
                    _binding.resetCardStateBtn.setEnableWithBackgroundCue(true)
                }
            }
        }
        _binding.flickCardBtn.setOnClickListener { view ->
            _learningCardBindingHelper.flickFront(LearningCardBindingHelper.COLLIDE_WEST)
            view.setEnableWithBackgroundCue(false)
        }
        _binding.resetCardStateBtn.setOnClickListener { view ->
            _learningCardBindingHelper.resetVisual()
            view.setEnableWithBackgroundCue(false)
            _binding.flickCardBtn.setEnableWithBackgroundCue(true)
        }
    }
}