package com.tegaoteam.application.tegao.ui.component.onlineocr

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tegaoteam.application.tegao.databinding.DialogBottomsheetImageocrBinding
import com.tegaoteam.application.tegao.ui.shared.preset.DialogPreset
import timber.log.Timber

class ImageOCRDialogFragment(
    private val onUsingAllRecognizedTextListener: ((String?) -> Unit)? = null
): BottomSheetDialogFragment() {
    private lateinit var _binding: DialogBottomsheetImageocrBinding
    private lateinit var _viewModel: ImageOCRDialogViewModel
    private lateinit var _selectImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogBottomsheetImageocrBinding.inflate(layoutInflater, container, false)
        _viewModel = ViewModelProvider(this)[ImageOCRDialogViewModel::class]
        _selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { selectingImage(it) } }

        initDialog()

        return _binding.root
    }

    private fun initDialog() {
        _viewModel.recognizedText.observe(viewLifecycleOwner) {
            _binding.recognizedTextTxt.text = it
        }

        _binding.apply {
            hasRecognizedText = _viewModel.hasRecognizedText
            imagePreviewImg.setOnClickListener { v ->
                DialogPreset.quickView(
                    requireContext(),
                    ImageView(requireContext()).apply { setImageURI(_viewModel.selectedImageUri) }
                )
            }
            selectImageBtn.setOnClickListener { _selectImageLauncher.launch("image/*") }
            usingAllBtn.setOnClickListener { onUsingAllRecognizedTextListener?.invoke(_viewModel.recognizedText.value) }
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
    }

    private fun selectingImage(uri: Uri) {
        _binding.imagePreviewImg.setImageURI(uri)
        _viewModel.requestImageOCR(uri)
    }
}