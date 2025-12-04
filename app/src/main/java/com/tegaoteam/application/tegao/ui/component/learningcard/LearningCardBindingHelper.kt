package com.tegaoteam.application.tegao.ui.component.learningcard

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.databinding.ViewLearningCardBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.component.generics.InputBarView
import com.tegaoteam.application.tegao.ui.component.handwriting.WritingViewBindingHelper
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.toggleVisibility

class LearningCardBindingHelper(
    private val activity: AppCompatActivity,
    private val lifecycleOwner: LifecycleOwner,
    private var cardEntry: CardEntry = CardEntry.default(),
    val binding: ViewLearningCardBinding? = null
) {
    private val themedContext =
        ContextThemeWrapper(activity, R.style.Theme_Tegao_LearningCardText_Default)
    private val defaultContext =
        ContextThemeWrapper(activity, R.style.Theme_Tegao_ContentText_Normal)
    private var _inputBarView: InputBarView? = null
    private lateinit var _collideCue: CollideCueController
    var currentMode = MODE_PREVIEW
        private set

    fun getCardEntry() = cardEntry.copy()

    fun setCardEntry(newCardEntry: CardEntry) {
        cardEntry = newCardEntry
        bindOnMode(currentMode)
        resetVisual()
    }

    fun setMode(mode: Int) {
        if (mode !in listOf(MODE_PREVIEW, MODE_NO_RATING, MODE_SRS_RATING)) currentMode = MODE_NO_RATING else currentMode = mode
    }

    fun bindOnMode(mode: Int): View? {
        if (mode !in listOf(MODE_PREVIEW, MODE_NO_RATING, MODE_SRS_RATING)) return null
        currentMode = mode

        val bindComp = binding?: ViewLearningCardBinding.inflate(LayoutInflater.from(activity), null, false)
        resetBinding(bindComp)
        bindContents(bindComp)
        bindCardType(bindComp, cardEntry.type)
        bindBehavior(bindComp, currentMode)

        return bindComp.root
    }

    private fun resetBinding(binding: ViewLearningCardBinding) {
        binding.apply {
            loCardFrontContentsLst.removeAllViews()
            loCardBackContentsLst.removeAllViews()
            loFrontFooterBarFrm.removeAllViews()
            loBackFooterBarFrm.removeAllViews()
        }
        _inputBarView?.clearInput()
        resetVisual()
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
                    addView(_inputBarView?.view)
                    toggleVisibility(true)
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
            MODE_NO_RATING, MODE_SRS_RATING -> {
                setupCollideCue(binding)
                binding.loCardFrontFlk.apply {
                    flickable = (cardEntry.type == CARDTYPE_FLASHCARD)
                    enableFlickAway = true
                    ignoreFinalCollidingOnLongCollide = true
                }
                binding.loCardBackFlk.apply {
                    flickable = true
                    enableFlickAway = true
                    ignoreFinalCollidingOnLongCollide = true
                }
            }
        }
        binding.executePendingBindings()
    }

    private fun setupCollideCue(binding: ViewLearningCardBinding) {
        binding.collideCueIcl.let {
            _collideCue = CollideCueController(it)
            it.root.toggleVisibility(true)
        }
        COLLIDE_ALL.forEach {
            if (binding.loCardFrontFlk.getOnCollideListener(it) == null) { setOnFrontCollideListener(it) }
            if (binding.loCardBackFlk.getOnCollideListener(it) == null) { setOnBackCollideListener(it) }
            if (binding.loCardFrontFlk.getOnFinalCollideListener(it) == null) { setOnFrontFinalCollideListener(it) }
            if (binding.loCardBackFlk.getOnFinalCollideListener(it) == null) { setOnBackFinalCollideListener(it) }
        }
    }

    fun setOnFrontCollideListener(vararg sides: Int, lambda: (() -> Unit)? = null) {
        sides.forEach { binding?.loCardFrontFlk!!.setOnCollideListener(it) {
            _collideCue.showCue(it)
            lambda?.invoke()
        } }
    }
    fun setOnFrontFinalCollideListener(vararg sides: Int, lambda: (() -> Unit)? = null) {
        sides.forEach { binding?.loCardFrontFlk!!.setOnFinalCollideListener(it) {
            _collideCue.apply {
                showCue(COLLIDE_NONE)
                if (currentMode == MODE_SRS_RATING) applyTint(CollideCueController.MODE_SRS)
            }
            lambda?.invoke()
        } }
    }

    fun setOnBackCollideListener(vararg sides: Int, lambda: (() -> Unit)? = null) {
        sides.forEach { binding?.loCardBackFlk!!.setOnCollideListener(it) {
            _collideCue.showCue(it)
            lambda?.invoke()
        } }
    }
    fun setOnBackFinalCollideListener(vararg sides: Int, lambda: (() -> Unit)? = null) {
        sides.forEach { binding?.loCardBackFlk!!.setOnFinalCollideListener(it) {
            _collideCue.showCue(COLLIDE_NONE)
            lambda?.invoke()
        } }
    }

    fun resetVisual() {
        if (::_collideCue.isInitialized) _collideCue.apply {
            clearCue()
            applyTint(CollideCueController.MODE_NEUTRAL)
        }
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

    fun flickFront(colliding: Int) { binding?.loCardFrontFlk?.doFlick(colliding) }
    fun flickBack(colliding: Int) { binding?.loCardBackFlk?.doFlick(colliding) }

    private fun initInputBar() {
        val addonRepo = AddonHub()
        if (_inputBarView == null) _inputBarView = InputBarView(
            context = activity,
            lifecycleOwner = lifecycleOwner,
            addonRepo = addonRepo
        )
        activity.findViewById<FrameLayout>(R.id.unv_customInputHolder_frm)?.let {
            if (_inputBarView?.isHandwritingEnabled?: false) {
                WritingViewBindingHelper.fullSuggestionBoard(
                    addonRepo = addonRepo,
                    activity = activity,
                    linkedEditText = _inputBarView!!.getEditTextView(),
                    boardHolder = it,
                    switchButtonBinding = _inputBarView!!.getSwitchButton()
                )
            }
        }
    }

    fun getAnswer(): String? = _inputBarView?.getInputValue()

    @SuppressLint("SetTextI18n")
    fun submitAnswer(ans: String?) {
        if (cardEntry.type == CARDTYPE_ANSWERCARD) {
            binding?.loCardBackContentsLst?.apply {
                addView(AppCompatTextView(defaultContext).apply {
                    setBackgroundResource(R.drawable.neutral_stroke_underline)
                    setPadding(0, 0, 0, dpToPixel(8f).toInt())
                    gravity = Gravity.CENTER
                    text = "$ans\n${if (ans == cardEntry.answer || cardEntry.answer == null) "✔" else "✘" }\n${cardEntry.answer}"
                }, 0)
                toggleVisibility(true)
            }
        }
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
        val COLLIDE_ALL = intArrayOf(COLLIDE_NONE, COLLIDE_WEST, COLLIDE_NORTH, COLLIDE_EAST, COLLIDE_SOUTH)

        private val CARDTYPE_FLASHCARD = LearningCardConst.Type.TYPE_FLASHCARD.id
        private val CARDTYPE_ANSWERCARD = LearningCardConst.Type.TYPE_ANSWERCARD.id
    }
}