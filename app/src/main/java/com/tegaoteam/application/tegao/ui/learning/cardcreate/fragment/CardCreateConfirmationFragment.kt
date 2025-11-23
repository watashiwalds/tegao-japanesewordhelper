package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueInputBinding
import com.tegaoteam.application.tegao.ui.component.learningpack.LearningCardWrapper
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardPlaceholder
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import com.tegaoteam.application.tegao.utils.setSrcWithResId
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber
import kotlin.getValue

class CardCreateConfirmationFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueInputBinding
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    private lateinit var _frontRawEditText: EditText
    private lateinit var _backRawEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _parentViewModel.parsingCardPlaceholder()

        _binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_card_create_value_input,
            container,
            false
        )

        initView()

        return _binding.root
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_confirmation)

        val plc = _parentViewModel.parsedCardPlaceholder
        plc?.let {
            val pad16 = dpToPixel(16f).toInt()
            val themedContext = ContextThemeWrapper(requireContext(), R.style.Theme_Tegao_ContentText_Normal)
            val edtLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(pad16, 0, 0, pad16)
            }
            _frontRawEditText = AppCompatEditText(themedContext).apply {
                layoutParams = edtLayoutParams
                isSingleLine = false
                gravity = Gravity.START or Gravity.TOP
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
            _backRawEditText = AppCompatEditText(themedContext).apply {
                layoutParams = edtLayoutParams
                isSingleLine = false
                gravity = Gravity.START or Gravity.TOP
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }

            _binding.loInputFieldListLst.apply {
                removeAllViews()
                addView(AppCompatTextView(themedContext).apply {
                    text = getString(R.string.card_create_info_general).format(
                        _parentViewModel.cardTypeChipItems.find { it.first == plc.type }!!.second.split("\n")[0],
                        _parentViewModel.cardGroups.value?.find { it.groupId == plc.groupId }!!.label
                    )
                    setPadding(0, pad16, 0, pad16)
                })
                plc.answer?.let { addView(AppCompatTextView(themedContext).apply {
                    text = getString(R.string.card_create_info_answer).format(it)
                    setPadding(0, 0, 0, pad16)
                }) }
                addView(AppCompatTextView(themedContext).apply{ text = getString(R.string.card_create_info_front) })
                addView(_frontRawEditText.apply {
                    editableText.append(plc.front)
                })
                addView(AppCompatTextView(themedContext).apply{ text = getString(R.string.card_create_info_back) })
                addView(_backRawEditText.apply {
                    editableText.append(plc.back)
                })
            }
        }

        _binding.placeholderQabBtn.apply {
            setSrcWithResId(R.drawable.ftc_bold_view_128)
            setOnClickListener {
                updateContents()
                val preview = LearningCardWrapper(
                    context = requireContext(),
                    lifecycleOwner = viewLifecycleOwner,
                    cardEntry = CardPlaceholder.toDomainCardEntry(_parentViewModel.parsedCardPlaceholder!!),
                    mode = LearningCardWrapper.MODE_PREVIEW
                ).inflate()
                DialogPreset.quickView(requireContext(), preview, R.string.card_create_preview_gesture_hint)
            }
            toggleVisibility(true)
        }
        _binding.nextBtn.setOnClickListener {
            DialogPreset.requestConfirmation(
                requireContext(),
                0,
                _frontRawEditText.text.toString()
            )
        }
    }

    private fun updateContents() {
        _parentViewModel.updatePlaceholderContents(_frontRawEditText.text.toString(), _backRawEditText.text.toString())
    }
}