package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionRunBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.component.learningcard.LearningCardBindingHelper
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class CardStackDisplayManager(val context: Context, val learnMode: Int, private val binding: FragmentLearningSessionRunBinding) {
    private lateinit var firstView: LearningCardBindingHelper
    private lateinit var secondView: LearningCardBindingHelper
    private val currentView = mutableListOf<LearningCardBindingHelper>()

    fun getCurrentTopView() = currentView.firstOrNull()
    private fun getTheOtherView() = listOf(firstView, secondView).first { it != getCurrentTopView() }

    fun initComponents(lifecycleOwner: LifecycleOwner) {
        firstView = LearningCardBindingHelper(
            context = context,
            lifecycleOwner = lifecycleOwner,
            binding = binding.viewLearningCardFirst
        )
        secondView = LearningCardBindingHelper(
            context = context,
            lifecycleOwner = lifecycleOwner,
            binding = binding.viewLearningCardSecond
        )
        firstView.setMode(learnMode)
        secondView.setMode(learnMode)
        currentView.apply {
            clear()
            add(firstView)
        }
    }

    fun getCardBinders() = listOf(firstView, secondView)

    fun prepareDisplay(currentCard: CardEntry?, nextCard: CardEntry?) {
        // first assign
        if (currentView.isEmpty()) {
            currentView.add(firstView)
            firstView.setCardEntry(currentCard!!)
            nextCard?.let { secondView.setCardEntry(nextCard) }?: { secondView.binding!!.root.toggleVisibility(false) }
            return
        }

        //on chain assign
        val swappedView = getTheOtherView()
        currentView.apply {
            clear()
            add(swappedView)
        }
        getCurrentTopView()?.binding!!.root.bringToFront()
        currentCard?.let { if (getCurrentTopView()?.getCardEntry()?.cardId != currentCard.cardId) getCurrentTopView()?.setCardEntry(currentCard) }
        nextCard?.let { getTheOtherView().setCardEntry(nextCard) }

        // either empty -> session ending (soon)
        if (currentCard == null) {
            getCurrentTopView()?.binding!!.root.toggleVisibility(false)
        }
        if (nextCard == null) {
            getTheOtherView().binding!!.root.toggleVisibility(false)
        }
    }
}