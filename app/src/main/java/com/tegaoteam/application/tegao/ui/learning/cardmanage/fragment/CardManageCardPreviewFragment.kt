package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.databinding.FragmentCardManagePreviewBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.component.learningcard.FlickableConstraintLayout
import com.tegaoteam.application.tegao.ui.component.learningcard.LearningCardBindingHelper
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.utils.AppToast
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
        if (!::_learningCardBindingHelper.isInitialized) _learningCardBindingHelper = LearningCardBindingHelper(requireContext(), viewLifecycleOwner, _cardEntry, _binding.viewLearningCard)
        _learningCardBindingHelper.bindOnMode(LearningCardBindingHelper.MODE_NO_RATING)
        _binding.viewLearningCard.apply {
            FlickableConstraintLayout.apply {
                loCardFrontFlk.setOnCollideListener(
                    COLLIDING_NORTH,
                    COLLIDING_WEST,
                    COLLIDING_EAST,
                    COLLIDING_SOUTH
                ) {
                    AppToast.show("COLLIDE", AppToast.LENGTH_SHORT)
                }
                loCardFrontFlk.setOnFinalCollideListener(
                    COLLIDING_NORTH,
                    COLLIDING_WEST,
                    COLLIDING_EAST,
                    COLLIDING_SOUTH
                ) {
                    _binding.resetCardStateBtn.setEnableWithBackgroundCue(true)
                }
            }
        }
        _binding.resetCardStateBtn.setOnClickListener { view ->
            _learningCardBindingHelper.resetVisual()
            view.setEnableWithBackgroundCue(false)
        }
    }
}