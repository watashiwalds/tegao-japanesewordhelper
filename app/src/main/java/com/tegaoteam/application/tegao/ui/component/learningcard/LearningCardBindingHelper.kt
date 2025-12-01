package com.tegaoteam.application.tegao.ui.component.learningcard

import android.content.Context
import android.view.Gravity
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

class LearningCardBindingHelper(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    val cardEntry: CardEntry,
    val binding: ViewLearningCardBinding? = null
) {
    private val themedContext =
        ContextThemeWrapper(context, R.style.Theme_Tegao_LearningCardText_Default)
    private val defaultContext =
        ContextThemeWrapper(context, R.style.Theme_Tegao_ContentText_Normal)
    private lateinit var _inputBarView: InputBarView
    var currentMode = MODE_PREVIEW
        private set

    fun bindOnMode(mode: Int): View? {
        if (mode !in listOf(MODE_PREVIEW, MODE_NO_RATING, MODE_SRS_RATING)) return null
        currentMode = mode

        val bindComp = binding?: ViewLearningCardBinding.inflate(LayoutInflater.from(context), null, false)
        bindContents(bindComp)
        bindCardType(bindComp, cardEntry.type)
        bindBehavior(bindComp, currentMode)

        return bindComp.root
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
            binding.loFrontFooterBarFrm.apply {
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

    private fun bindCardType(binding: ViewLearningCardBinding, cardType: Int) {
        when (cardType) {
            CARDTYPE_ANSWERCARD -> {
                initInputBar()
                binding.loFrontFooterBarFrm.apply {
                    removeAllViews()
                    addView(_inputBarView.view)
                    toggleVisibility(true)
                }
                binding.loBackFooterBarFrm.apply {
                    removeAllViews()
                    cardEntry.answer?.let {
                        addView(AppCompatTextView(defaultContext).apply {
                            gravity = Gravity.CENTER
                            text = it
                        })
                        toggleVisibility(true)
                    }
                }
            }
        }
        binding.executePendingBindings()
    }

    private fun bindBehavior(binding: ViewLearningCardBinding, mode: Int) {
        binding.apply {
            loCardFrontFlk.collideDpPadding = 24f
            loCardBackFlk.collideDpPadding = 24f
        }
        when (mode) {
            MODE_PREVIEW -> {
                binding.loCardFrontFlk.flickable = true
            }
            MODE_NO_RATING -> {
                binding.loCardFrontFlk.apply {
                    flickable = true
                    enableFlickAway = true
                }
                binding.loCardBackFlk.apply {
                    flickable = true
                    enableFlickAway = true
                }
            }
        }
        binding.executePendingBindings()
    }

    fun resetVisual() {
        binding?.apply {
            loCardFrontFlk.apply {
                animate()
                    .translationX(0f)
                    .translationY(0f)
                    .start()
                toggleVisibility(true)
            }
            loCardBackFlk.apply {
                animate()
                    .translationX(0f)
                    .translationY(0f)
                    .start()
                toggleVisibility(true)
            }
        }
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

        const val COLLIDE_NONE = FlickableConstraintLayout.COLLIDING_NONE
        const val COLLIDE_WEST = FlickableConstraintLayout.COLLIDING_WEST
        const val COLLIDE_NORTH = FlickableConstraintLayout.COLLIDING_NORTH
        const val COLLIDE_EAST = FlickableConstraintLayout.COLLIDING_EAST
        const val COLLIDE_SOUTH = FlickableConstraintLayout.COLLIDING_SOUTH
        const val COLLIDE_ALL = COLLIDE_NONE - 1

        private val CARDTYPE_FLASHCARD = LearningCardConst.Type.TYPE_FLASHCARD.id
        private val CARDTYPE_ANSWERCARD = LearningCardConst.Type.TYPE_ANSWERCARD.id
    }
}