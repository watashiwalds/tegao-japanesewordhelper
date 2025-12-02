package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

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
import com.tegaoteam.application.tegao.domain.model.CardEntry
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
        _viewModel.sessionMode = if (_parentViewModel.noRatingMode) LearningCardBindingHelper.MODE_NO_RATING else LearningCardBindingHelper.MODE_SRS_RATING

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

    private lateinit var _cardStackDisplayManager: CardStackDisplayManager
    private fun bindingStartCards() {
        _cardStackDisplayManager = CardStackDisplayManager(requireContext(), _viewModel.sessionMode, _binding)
        _cardStackDisplayManager.initComponents(viewLifecycleOwner)

        _cardStackDisplayManager.getCardBinders().forEach {
            it.apply {
                setOnBackFinalCollideListener(RATING_EASY) { finishACards(getCardEntry().cardId, RATING_EASY) }
                setOnBackFinalCollideListener(RATING_GOOD) { finishACards(getCardEntry().cardId, RATING_GOOD) }
                setOnBackFinalCollideListener(RATING_HARD) { finishACards(getCardEntry().cardId, RATING_HARD) }
                setOnBackFinalCollideListener(RATING_FORGET) { finishACards(getCardEntry().cardId, RATING_FORGET) }
            }
        }
        when (_viewModel.sessionMode) {
            LearningCardBindingHelper.MODE_SRS_RATING -> {
                _cardStackDisplayManager.getCardBinders().forEach {
                    it.apply {
                        setOnFrontFinalCollideListener(*LearningCardBindingHelper.COLLIDE_ALL) {
                            showFooterControl(FOOTER_BACKRATING, null)
                        }
                    }
                }
            }
        }

        val firstCard = _viewModel.sessionDeck.removeAt(0)
        inLineCard = if (_viewModel.sessionDeck.isNotEmpty()) _viewModel.sessionDeck.removeAt(0) else null
        _cardStackDisplayManager.prepareDisplay(firstCard, inLineCard)

        val footerType = when (firstCard.type) {
            LearningCardConst.Type.TYPE_FLASHCARD.id -> FOOTER_FRONTFLASH
            LearningCardConst.Type.TYPE_ANSWERCARD.id -> FOOTER_FRONTANSWER
            else -> FOOTER_FRONTFLASH
        }
        showFooterControl(footerType, _cardStackDisplayManager.getCurrentTopView()!!)
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
            ratingEasyBtn.setOnClickListener { _cardStackDisplayManager.getCurrentTopView()?.flickBack(RATING_EASY) }
            ratingGoodBtn.setOnClickListener { _cardStackDisplayManager.getCurrentTopView()?.flickBack(RATING_GOOD) }
            ratingHardBtn.setOnClickListener { _cardStackDisplayManager.getCurrentTopView()?.flickBack(RATING_HARD) }
            ratingForgetBtn.setOnClickListener { _cardStackDisplayManager.getCurrentTopView()?.flickBack(RATING_FORGET) }
        }
    }

    private val RATING_EASY = LearningCardBindingHelper.COLLIDE_NORTH
    private val RATING_GOOD = LearningCardBindingHelper.COLLIDE_WEST
    private val RATING_HARD = LearningCardBindingHelper.COLLIDE_EAST
    private val RATING_FORGET = LearningCardBindingHelper.COLLIDE_SOUTH
    private var inLineCard: CardEntry? = null
    private var preFinishSession = false
    private fun finishACards(cardId: Long, rating: Int) {
        //TODO: Calculate and update repeat entry by using SRS algorithm

        //if there is no other card to go through, end the session and go to metrics fragment
        if (_viewModel.sessionDeck.isEmpty()) {
            if (!preFinishSession) preFinishSession = true
            else findNavController().navigate(LearningSessionRunFragmentDirections.actionLearningSessionRunFragmentToLearningSessionMetricsFragment())
        } else {
            preFinishSession = false
        }

        //swapping cardView display order for "stack" immersion
        val nowCard = inLineCard?.copy()
        inLineCard = if (_viewModel.sessionDeck.isNotEmpty()) _viewModel.sessionDeck.removeAt(0) else null
        _cardStackDisplayManager.prepareDisplay(nowCard, inLineCard)

        //change footer control layout according to current front card
        val footerType = when (nowCard?.type) {
            LearningCardConst.Type.TYPE_FLASHCARD.id -> FOOTER_FRONTFLASH
            LearningCardConst.Type.TYPE_ANSWERCARD.id -> FOOTER_FRONTANSWER
            else -> FOOTER_FRONTFLASH
        }
        showFooterControl(footerType, _cardStackDisplayManager.getCurrentTopView()!!)

        Timber.i("Card deck remain ${_viewModel.sessionDeck.size}")
    }

    private val FOOTER_FRONTFLASH = 0
    private val FOOTER_BACKRATING = 1
    private val FOOTER_FRONTANSWER = 2
    private fun showFooterControl(footerType: Int, relatedCardBinder: LearningCardBindingHelper?) {
        _binding.apply {
            loBackControlFrm.toggleVisibility(false)
            loFrontControlFrm.toggleVisibility(false)
            when (footerType) {
                FOOTER_FRONTFLASH -> {
                    loFrontControlFrm.toggleVisibility(true)
                    frontCardActionBtn.apply {
                        setEnableWithBackgroundCue(true)
                        text = getString(R.string.card_learn_frontControl_doFlick)
                        setOnClickListener { relatedCardBinder?.flickFront(LearningCardBindingHelper.COLLIDE_WEST) }
                    }
                }
                FOOTER_FRONTANSWER -> {
                    loFrontControlFrm.toggleVisibility(true)
                    frontCardActionBtn.apply {
                        setEnableWithBackgroundCue(true)
                        text = getString(R.string.card_learn_frontControl_doAnswer)
                        setOnClickListener { relatedCardBinder?.submitAnswer(relatedCardBinder.getAnswer()) }
                    }
                }
                FOOTER_BACKRATING -> {
                    Timber.i("Backrating invoked")
                    loBackControlFrm.toggleVisibility(true)
                }
            }
            executePendingBindings()
        }
    }
}