package com.tegaoteam.application.tegao.ui.component.onlineocr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.DialogBottomsheetImageocrBinding
import timber.log.Timber

class ImageOCRDialogFragment: BottomSheetDialogFragment() {
    private lateinit var _binding: DialogBottomsheetImageocrBinding
    private lateinit var _viewModel: ImageOCRDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogBottomsheetImageocrBinding.inflate(layoutInflater, container, false)
        _viewModel = ViewModelProvider(this)[ImageOCRDialogViewModel::class]

        initDialog()

        return _binding.root
    }

    private fun initDialog() {
        _viewModel.recognizedText.observe(viewLifecycleOwner) {
            _binding.recognizedTextTxt.text = it
        }

        _binding.apply {
            hasRecognizedText = _viewModel.hasRecognizedText
            selectImageBtn.setOnClickListener {
                Timber.d("TODO: Request image from OS gallery")
            }
            usingAllBtn.setOnClickListener {
                Timber.d("TODO: Find a way to return recognized text straight to assigned EditText/Fragment/Activity")
            }
            executePendingBindings()
        }
    }
}