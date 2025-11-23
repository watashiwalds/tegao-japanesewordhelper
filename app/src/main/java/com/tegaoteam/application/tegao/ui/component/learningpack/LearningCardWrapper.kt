package com.tegaoteam.application.tegao.ui.component.learningpack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.databinding.ViewLearningCardBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.component.generics.InputBarView
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.utils.toggleVisibility

class LearningCardWrapper(private val context: Context, private val lifecycleOwner: LifecycleOwner, private val cardEntry: CardEntry, val mode: Int) {
    private val themedContext = ContextThemeWrapper(context, R.style.Theme_Tegao_ContentText_Normal)
    private lateinit var _inputBarView: InputBarView

    fun inflate(): View {
        val binding = ViewLearningCardBinding.inflate(LayoutInflater.from(context), null, false)
        bindContents(binding)
        bindTypeDisplay(binding, cardEntry.type)
        bindModeFunctions(binding, mode)

        return binding.root
    }

    private fun bindContents(binding: ViewLearningCardBinding) {
        // Front
        binding.loCardFrontContentsLst.apply {
            removeAllViews()
            addView(AppCompatTextView(themedContext).apply {
                text = cardEntry.front
            })
        }
        // Back
        cardEntry.answer?.let {
            initInputBar()
            binding.loFrontHeaderBarFrm.apply {
                removeAllViews()
                addView(_inputBarView.view)
                toggleVisibility(true)
            }
        }
        binding.loCardBackContentsLst.apply {
            removeAllViews()
            addView(AppCompatTextView(themedContext).apply {
                text = cardEntry.back
            })
        }
        binding.executePendingBindings()
    }

    private fun bindTypeDisplay(binding: ViewLearningCardBinding, cardType: Int) {
        when (cardType) {
            CARDTYPE_ANSWERCARD -> {
                initInputBar()
                binding.loFrontHeaderBarFrm.apply {
                    removeAllViews()
                    addView(_inputBarView.view)
                    toggleVisibility(true)
                }
                binding.loBackFooterBarFrm.apply {
                    removeAllViews()
                    cardEntry.answer?.let { addView(AppCompatTextView(themedContext).apply {
                        textAlignment = AppCompatTextView.TEXT_ALIGNMENT_CENTER
                        text = it
                    })}
                }
            }
        }
        binding.executePendingBindings()
    }

    private fun bindModeFunctions(binding: ViewLearningCardBinding, mode: Int) {
        when (mode) {
            MODE_PREVIEW -> {
                binding.loCardFrontFlk.flickable = true
            }
        }
        binding.executePendingBindings()
    }

    private fun initInputBar() {
        _inputBarView = InputBarView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            addonRepo = AddonHub()
        )
    }

    companion object {
        const val MODE_PREVIEW = 0
        const val MODE_NO_RATING = 1
        const val MODE_SRS_RATING = 2

        private val CARDTYPE_FLASHCARD = LearningCardConst.Type.TYPE_FLASHCARD.id
        private val CARDTYPE_ANSWERCARD = LearningCardConst.Type.TYPE_ANSWERCARD.id
    }
}