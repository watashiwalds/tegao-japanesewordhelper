package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionRunBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.component.generics.InputBarView
import com.tegaoteam.application.tegao.ui.component.learningcard.LearningCardBindingHelper
import com.tegaoteam.application.tegao.utils.toggleVisibility

class CardStackDisplayManager(val activity: AppCompatActivity, val learnMode: Int, private val binding: FragmentLearningSessionRunBinding) {
    private lateinit var firstView: LearningCardBindingHelper
    private lateinit var secondView: LearningCardBindingHelper
    private val currentView = mutableListOf<LearningCardBindingHelper>()

    fun getCurrentTopView() = currentView.firstOrNull()
    private fun getTheOtherView() = listOf(firstView, secondView).first { it != getCurrentTopView() }

    fun initComponents(lifecycleOwner: LifecycleOwner) {
        val sharedInputBarView = InputBarView(
            context = activity,
            lifecycleOwner = lifecycleOwner,
            addonRepo = AddonHub()
        )
        firstView = LearningCardBindingHelper(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            binding = binding.viewLearningCardFirst,
            inputBarView = sharedInputBarView
        )
        secondView = LearningCardBindingHelper(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            binding = binding.viewLearningCardSecond,
            inputBarView = sharedInputBarView
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
        getCurrentTopView()?.let { it.resetBinding(it.binding!!) }
        val swappedView = getTheOtherView()
        currentView.apply {
            clear()
            add(swappedView)
        }
        getCurrentTopView()?.binding!!.root.bringToFront()
        currentCard?.let { getCurrentTopView()?.setCardEntry(currentCard) }
        if (nextCard == null) getTheOtherView().hideVisual()
    }
}