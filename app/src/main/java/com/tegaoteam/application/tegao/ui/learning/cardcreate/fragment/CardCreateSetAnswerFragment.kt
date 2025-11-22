package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueInputBinding
import com.tegaoteam.application.tegao.ui.component.generics.InputBarView
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import kotlin.getValue

class CardCreateSetAnswerFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueInputBinding
    private lateinit var _inputBarView: InputBarView
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_card_create_value_input,
            container,
            false
        )

        initVariables()
        initView()

        return _binding.root
    }

    private fun initVariables() {
        _inputBarView = InputBarView(requireContext(), this, AddonHub())
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_answer)
        _binding.loInputFieldListLst.apply {
            removeAllViews()
            addView(_inputBarView.view)
        }
        _binding.executePendingBindings()

        _binding.nextBtn.setOnClickListener {
            DialogPreset.requestConfirmation(
                context = requireContext(),
                title = 0,
                message = _inputBarView.getInputValue()
            )
        }
    }
}