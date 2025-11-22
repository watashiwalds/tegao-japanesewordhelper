package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueInputBinding
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.utils.dpToPixel
import kotlin.getValue

class CardCreateConfirmationFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueInputBinding
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

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
        val pad16 = dpToPixel(16f).toInt()
        plc?.let {
            _binding.loInputFieldListLst.apply {
                removeAllViews()
                val themedContext = ContextThemeWrapper(requireContext(), R.style.Theme_Tegao_ContentText_Normal)
                addView(AppCompatTextView(themedContext).apply {
                    text = getString(R.string.card_create_info_general).format(
                        _parentViewModel.cardTypeChipItems.find { it.first == plc.type }!!.second.split("\n")[0],
                        _parentViewModel.cardGroups.value?.find { it.groupId == plc.groupId }!!.label
                    )
                    setPadding(0, pad16, 0, pad16)
                })
                plc.answer?.let { addView(TextView(themedContext).apply {
                    text = getString(R.string.card_create_info_answer).format(it)
                    setPadding(0, 0, 0, pad16)
                }) }
                addView(AppCompatTextView(themedContext).apply{ text = getString(R.string.card_create_info_front) })
                addView(AppCompatTextView(themedContext).apply{
                    text = plc.front
                    setPadding(pad16, 0, 0, pad16)
                })
                addView(AppCompatTextView(themedContext).apply{ text = getString(R.string.card_create_info_back) })
                addView(AppCompatTextView(themedContext).apply{
                    text = plc.back
                    setPadding(pad16, 0, 0, pad16)
                })
            }
        }
    }
}