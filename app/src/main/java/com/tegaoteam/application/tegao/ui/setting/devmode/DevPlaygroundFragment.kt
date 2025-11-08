package com.tegaoteam.application.tegao.ui.setting.devmode

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentDevPlaygroundBinding
import com.tegaoteam.application.tegao.ui.component.generics.SwitchButtonInfo
import com.tegaoteam.application.tegao.ui.component.handwriting.WritingViewController
import timber.log.Timber

class DevPlaygroundFragment : Fragment() {
    private lateinit var _binding: FragmentDevPlaygroundBinding
    private lateinit var _writingController: WritingViewController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dev_playground, container, false)
        _writingController = WritingViewController(
            writingView = _binding.loWritingViewIcl.writingPadWrv,
            onStrokeFinished = { bitmap -> Timber.i("Bitmap received with value ${bitmap}") },
            writingBoard = _binding.loWritingViewIcl,
            editText = _binding.testEdittextEdt
        )

        _binding.testPushswitch.apply {
            switchInfo = SwitchButtonInfo(
                iconResId = R.drawable.ftc_round_tick_128,
                switchState = MutableLiveData<Boolean>().apply { value = false },
                onStateChangedListener = { state ->
                    _writingController.toggleWritingMode(state)
                    activity?.apply {
                        if (currentFocus == _binding.testEdittextEdt)
                            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                                if (state) hideSoftInputFromWindow(_binding.testEdittextEdt.windowToken, 0)
                                else showSoftInput(_binding.testEdittextEdt, 0)
                            }
                    }
                    Timber.i("Writing mode toggled with state [$state]")
                }
            )
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        // Inflate the layout for this fragment
        return _binding.root
    }
}