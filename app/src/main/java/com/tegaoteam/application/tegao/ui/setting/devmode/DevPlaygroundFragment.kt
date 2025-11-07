package com.tegaoteam.application.tegao.ui.setting.devmode

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentDevPlaygroundBinding
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
            binding = _binding.loWritingViewIcl,
            editText = _binding.testEdittextEdt
        )

        // Inflate the layout for this fragment
        return _binding.root
    }
}