package com.tegaoteam.application.tegao.ui.component.handwriting

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ViewButtonPushswitchIcononlyBinding
import com.tegaoteam.application.tegao.databinding.ViewWritingBoardFullBinding
import com.tegaoteam.application.tegao.domain.repo.AddonRepo
import com.tegaoteam.application.tegao.ui.component.generics.SwitchButtonInfo
import timber.log.Timber

class WritingViewBindingHelper {
    companion object {
        /**
         * Binding a full functions WritingBoard according to provided variables.
         *
         * @return Inflated binding.root if successful, null otherwise.
         */
        fun fullSuggestionBoard(
            addonRepo: AddonRepo,
            activity: AppCompatActivity,
            linkedEditText: EditText,
            boardHolder: ViewGroup,
            switchButtonBinding: ViewButtonPushswitchIcononlyBinding
        ): View? {
            if (addonRepo.isHandwritingAvailable()) {
                val handwritingBoardBinding = ViewWritingBoardFullBinding.inflate(LayoutInflater.from(activity))
                boardHolder.apply {
                    removeAllViews()
                    addView(handwritingBoardBinding.root)
                }

                var writingViewController: WritingViewController? = null
                writingViewController = WritingViewController(
                    writingView = handwritingBoardBinding.writingPadWrv,
                    writingBinding = handwritingBoardBinding,
                    onRequestRecognition = { bitmap -> addonRepo.handwritingAddonApi?.requestInputSuggestions(bitmap) },
                    editText = linkedEditText,
                    onStrokeFinished = { writingViewController!!.requestSuggestions() },
                    onEnterKeyPressed = null
                )
                addonRepo.handwritingAddonApi?.registerCallback { suggestions -> writingViewController.updateSuggestionsList(suggestions) }

                switchButtonBinding.apply {
                    switchInfo = SwitchButtonInfo(
                        iconResId = R.drawable.ftc_round_handwriting_128,
                        switchState = MutableLiveData<Boolean>().apply { value = switchInfo?.stateLiveData?.value?: false }
                    ).apply {
                        writingViewController.toggleWritingMode(stateLiveData.value)
                        onStateChangedListener = { switchState ->
                            writingViewController.toggleWritingMode(switchState)
                            activity.apply {
                                if (writingViewController.isEditTextEqual(currentFocus))
                                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                                        if (switchState) hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                                        else showSoftInput(currentFocus, 0)
                                    }
                            }
                        }
                    }
                    lifecycleOwner = activity
                    executePendingBindings()
                }
                return handwritingBoardBinding.root
            }
            return null
        }
    }
}