package com.tegaoteam.application.tegao.ui.homescreen.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.data.hub.TranslatorHub
import com.tegaoteam.application.tegao.databinding.FragmentMainTranslateBinding
import com.tegaoteam.application.tegao.domain.model.Translator
import com.tegaoteam.application.tegao.domain.repo.TranslatorRepo
import com.tegaoteam.application.tegao.ui.component.handwriting.WritingViewBindingHelper
import com.tegaoteam.application.tegao.ui.component.onlineocr.ImageOCRDialogFragment
import com.tegaoteam.application.tegao.ui.homescreen.MainActivityViewModel
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.LabelBank
import timber.log.Timber
import kotlin.getValue

class TranslateFragment: Fragment() {
    private lateinit var _binding: FragmentMainTranslateBinding
    private lateinit var _viewModel: TranslateFragmentViewModel
    private val _parentViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var _translatorRepo: TranslatorRepo
    private lateinit var _translator: Translator
    private lateinit var _imageOcrDialog: ImageOCRDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainTranslateBinding.inflate(layoutInflater, container, false)
        _translatorRepo = TranslatorHub()
        _imageOcrDialog = ImageOCRDialogFragment()
        _viewModel = ViewModelProvider(requireActivity(), TranslateFragmentViewModel.Companion.ViewModelFactory(_translatorRepo))[TranslateFragmentViewModel::class]

        initView()

        return _binding.root
    }

    override fun onResume() {
        _parentViewModel.fragmentChanged(R.id.main_translateFragment.toString())

        _viewModel.apply {
            sourceText?.let { _binding.sourceTextEdt.editableText.apply { clear(); append(it) } }
        }

        super.onResume()
    }

    private fun initView() {
        setupInputFunctions()
        setupTranslator()

        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.executePendingBindings()
    }

    private fun setupInputFunctions() {
        _binding.showHandwritingSwitch = _viewModel.isHandWritingEnable
        _binding.sourceTextEdt.doOnTextChanged { text, start, before, count -> _viewModel.sourceText = text.toString() }
        if (_viewModel.isHandWritingEnable.value) {
            WritingViewBindingHelper.fullSuggestionBoard(
                addonRepo = AddonHub(),
                activity = requireActivity() as AppCompatActivity,
                linkedEditText = _binding.sourceTextEdt,
                boardHolder = requireActivity().findViewById(R.id.unv_customInputHolder_frm),
                switchButtonBinding = _binding.switchHandwritingModeIcl
            )
        }
        _binding.ocrFromImageBtn.setOnClickListener {
            _imageOcrDialog.show(requireActivity().supportFragmentManager, "DIALOG_IMAGE_OCR")
        }
    }

    private fun setupTranslator() {
        _translator = _translatorRepo.getAvailableTranslators().first()
        val sourceLangs = _translator.supportedSourceLang
        val transLangs = _translator.supportedTransLang

        var prevSourceLang = LabelBank.getLanguageName(_viewModel.sourceLang)
        var prevTransLang = LabelBank.getLanguageName(_viewModel.transLang)

        _binding.apply {
            translateFromCtx.apply {
                setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>()).apply {
                    sourceLangs.forEach { LabelBank.getLanguageName(it)?.let { label -> add(label) } }
                })
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (focused) showDropDown() }
                onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 ->
                    Timber.i("Change FROM language to ${sourceLangs[position]}")
                    _viewModel.sourceLang = sourceLangs[position]
                }
                post {
                    setText(prevSourceLang?: adapter.getItem(0).toString(), false)
                    prevSourceLang?: run {_viewModel.sourceLang = sourceLangs.first()}
                }
            }
            translateToCtx.apply{
                setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>()).apply {
                    transLangs.forEach { LabelBank.getLanguageName(it)?.let { label -> add(label) } }
                })
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (focused) showDropDown() }
                onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 ->
                    Timber.i("Change TO language to ${transLangs[position]}")
                    _viewModel.transLang = transLangs[position]
                }
                post {
                    setText(prevTransLang?: adapter.getItem(0).toString(), false)
                    prevSourceLang?: run {_viewModel.transLang = transLangs.first()}
                }
            }
        }

        _viewModel.translator = _translator
        _viewModel.translateResult.observe(viewLifecycleOwner) {
            _binding.translatedTextTxt.text = it
        }

        _binding.translateTextBtn.setOnClickListener {
            _viewModel.requestTranslation()
        }
    }
}