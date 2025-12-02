package com.tegaoteam.application.tegao.ui.learning.cardlearn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionRunBinding
import com.tegaoteam.application.tegao.ui.component.learningcard.LearningCardBindingHelper
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.ui.learning.cardlearn.CardLearningViewModel
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import com.tegaoteam.application.tegao.utils.setEnableWithBackgroundCue
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class LearningSessionRunFragment: Fragment() {
    private lateinit var _binding: FragmentLearningSessionRunBinding
    private lateinit var _viewModel: LearningSessionRunViewModel
    private val _parentViewModel: CardLearningViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearningSessionRunBinding.inflate(layoutInflater, container, false)
        _viewModel = ViewModelProvider(this)[LearningSessionRunViewModel::class]

        initObservers()

        _parentViewModel.fetchSessionDeck()

        return _binding.root
    }

    private fun initObservers() {
        _parentViewModel.apply {
            sessionDeck.observe(viewLifecycleOwner) {
                if (_viewModel.sessionDeck.isEmpty()) _viewModel.sessionDeck.addAll(it)
                Timber.i("Received ${it.size} cards for deck")
                initView()
            }
        }
    }

    private fun initView() {
        _binding.apply {
            bindingStartCards()
            bindingStaticButtons()

            //GONE loading screen and ready to flick some cards
            loLoadingScreenFrm.toggleVisibility(false)
            executePendingBindings()
        }
    }

    private fun bindingStartCards() {
        cardViewOnFront = LearningCardBindingHelper(
            context = requireContext(),
            lifecycleOwner = viewLifecycleOwner,
            cardEntry = _viewModel.sessionDeck.removeAt(0),
            binding = _binding.viewLearningCardFirst
        )
        if (_viewModel.sessionDeck.isNotEmpty()) {
            cardViewInBack = LearningCardBindingHelper(
                context = requireContext(),
                lifecycleOwner = viewLifecycleOwner,
                cardEntry = _viewModel.sessionDeck.removeAt(0),
                binding = _binding.viewLearningCardSecond
            )
        }
        Timber.i("Binding start cards result: \n${cardViewOnFront}, \n${cardViewInBack}. \nCard deck remain ${_viewModel.sessionDeck.size}")

        if (!_parentViewModel.noRatingMode) {
            listOf(cardViewOnFront, cardViewInBack).forEach {
                it?.apply {
                    setMode(LearningCardBindingHelper.MODE_SRS_RATING)
                    setOnFrontFinalCollideListener(*LearningCardBindingHelper.COLLIDE_ALL) {
                        showFooterControl(FOOTER_BACKRATING)
                    }
                    setOnBackFinalCollideListener(RATING_EASY) { finishACards(getCardEntry().cardId, RATING_EASY) }
                    setOnBackFinalCollideListener(RATING_GOOD) { finishACards(getCardEntry().cardId, RATING_GOOD) }
                    setOnBackFinalCollideListener(RATING_HARD) { finishACards(getCardEntry().cardId, RATING_HARD) }
                    setOnBackFinalCollideListener(RATING_FORGET) { finishACards(getCardEntry().cardId, RATING_FORGET) }
                }
            }
            cardViewOnFront?.bindOnMode(LearningCardBindingHelper.MODE_SRS_RATING)
            cardViewInBack?.bindOnMode(LearningCardBindingHelper.MODE_SRS_RATING)
        }

        showFooterControl(if (cardViewOnFront!!.getCardEntry().type == LearningCardConst.Type.TYPE_ANSWERCARD.id) FOOTER_FRONTANSWER else FOOTER_FRONTFLASH)
    }

    private fun bindingStaticButtons() {
        _binding.apply {
            pauseSessionBtn.setOnClickListener {
                DialogPreset.requestConfirmation(
                    context = requireContext(),
                    title = R.string.card_learn_pause_title,
                    message = R.string.card_learn_pause_message,
                    lambdaRun = { AppToast.show("TODO: Early finish this learning session", AppToast.LENGTH_SHORT) }
                )
            }
            ratingEasyBtn.setOnClickListener { cardViewOnFront?.flickBack(RATING_EASY) }
            ratingGoodBtn.setOnClickListener { cardViewOnFront?.flickBack(RATING_GOOD) }
            ratingHardBtn.setOnClickListener { cardViewOnFront?.flickBack(RATING_HARD) }
            ratingForgetBtn.setOnClickListener { cardViewOnFront?.flickBack(RATING_FORGET) }
        }
    }

    private val RATING_EASY = LearningCardBindingHelper.COLLIDE_NORTH
    private val RATING_GOOD = LearningCardBindingHelper.COLLIDE_WEST
    private val RATING_HARD = LearningCardBindingHelper.COLLIDE_EAST
    private val RATING_FORGET = LearningCardBindingHelper.COLLIDE_SOUTH
    private var cardViewOnFront: LearningCardBindingHelper? = null
    private var cardViewInBack: LearningCardBindingHelper? = null
    private var preFinishSession = false
    private fun finishACards(cardId: Long, rating: Int) {
        //TODO: Calculate and update repeat entry by using SRS algorithm

        //if there is no other card to go through, end the session and go to metrics fragment
        if (_viewModel.sessionDeck.isEmpty()) {
            if (!preFinishSession) preFinishSession = true
            else findNavController().navigate(LearningSessionRunFragmentDirections.actionLearningSessionRunFragmentToLearningSessionMetricsFragment())
        }

        //swapping cardView display order for "stack" immersion
        run { val temp = cardViewOnFront; cardViewOnFront = cardViewInBack; cardViewInBack = temp }
        cardViewOnFront?.binding!!.root.bringToFront()
        if (_viewModel.sessionDeck.isNotEmpty()) {
            cardViewInBack?.setCardEntry(_viewModel.sessionDeck.removeAt(0))
        }

        //change footer control accordingly to card type
        showFooterControl(if (cardViewOnFront!!.getCardEntry().type == LearningCardConst.Type.TYPE_ANSWERCARD.id) FOOTER_FRONTANSWER else FOOTER_FRONTFLASH)

        Timber.i("Card deck remain ${_viewModel.sessionDeck.size}")
    }

    private val FOOTER_FRONTFLASH = 0
    private val FOOTER_BACKRATING = 1
    private val FOOTER_FRONTANSWER = 2
    private fun showFooterControl(footerType: Int) {
        _binding.apply {
            loBackControlFrm.toggleVisibility(false)
            loFrontControlFrm.toggleVisibility(false)
            when (footerType) {
                FOOTER_FRONTFLASH -> {
                    loFrontControlFrm.toggleVisibility(true)
                    frontCardActionBtn.apply {
                        setEnableWithBackgroundCue(true)
                        text = getString(R.string.card_learn_frontControl_doFlick)
                        setOnClickListener { cardViewOnFront?.flickFront(LearningCardBindingHelper.COLLIDE_WEST) }
                    }
                }
                FOOTER_FRONTANSWER -> {
                    loFrontControlFrm.toggleVisibility(true)
                    frontCardActionBtn.apply {
                        setEnableWithBackgroundCue(true)
                        text = getString(R.string.card_learn_frontControl_doAnswer)
                        setOnClickListener { cardViewOnFront?.submitAnswer(cardViewOnFront?.getAnswer()) }
                    }
                }
                FOOTER_BACKRATING -> {
                    loBackControlFrm.toggleVisibility(true)
                }
            }
            executePendingBindings()
        }
    }
}