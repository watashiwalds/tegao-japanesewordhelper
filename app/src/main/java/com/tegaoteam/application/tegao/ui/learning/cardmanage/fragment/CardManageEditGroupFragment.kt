package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardManageDetailBinding
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset

class CardManageEditGroupFragment: Fragment() {
    private lateinit var _binding: FragmentCardManageDetailBinding
    private val _parentViewModel: CardManageActivityViewModel by activityViewModels()
    private var _groupId: Long? = null
    private lateinit var cardGroup: CardGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arriveArgs()
        _binding = FragmentCardManageDetailBinding.inflate(layoutInflater, container, false)

        initObservers()

        return _binding.root
    }

    private fun arriveArgs() {
        _groupId = CardManageEditGroupFragmentArgs.fromBundle(requireArguments()).groupId
    }

    private fun initObservers() {
        _parentViewModel.apply {
            cardGroups.observe(viewLifecycleOwner) {
                val res = it.firstOrNull { grp -> grp.groupId == _groupId }
                if (res == null) requireActivity().onBackPressedDispatcher.onBackPressed()
                else {
                    cardGroup = res
                    initView()
                }
            }
        }
    }

    private fun initView() {
        _binding.loFragmentTitleTxt.setText(R.string.card_manage_groupDetail_label)

        val pad16 = dpToPixel(16f).toInt()
        val themedContext = ContextThemeWrapper(requireContext(), R.style.Theme_Tegao_ContentText_Normal)
        val edtLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(pad16, 0, 0, pad16)
        }

        val labelEdt = AppCompatEditText(themedContext).apply {
            layoutParams = edtLayoutParams
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
        }

        _binding.loDetailListLst.apply {
            removeAllViews()
            addView(AppCompatTextView(themedContext).apply {
                setText(R.string.card_manage_groupDetail_field_name)
            })
            addView(labelEdt.apply {
                editableText.append(cardGroup.label)
            })
        }

        _binding.updateBtn.setOnClickListener {
            DialogPreset.requestConfirmation(
                context = requireContext(),
                title = 0,
                message = R.string.card_manage_update_title,
                lambdaRun = {
                    _parentViewModel.updateCardGroup(CardGroup(cardGroup.groupId, labelEdt.text.toString()))
                    AppToast.show(R.string.card_manage_update_confirm, AppToast.LENGTH_SHORT)
                }
            )
        }
        _binding.deleteBtn.apply {
            setOnClickListener {
                DialogPreset.requestConfirmation(
                    context = requireContext(),
                    title = getString(R.string.card_manage_delete_group_title, cardGroup.label),
                    message = R.string.card_manage_delete_group_message,
                    lambdaRun = { _parentViewModel.deleteGroup(cardGroup.groupId) }
                )
            }
        }

        _binding.executePendingBindings()
    }
}