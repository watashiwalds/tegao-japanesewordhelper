package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueInputBinding
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import kotlin.getValue

class CardCreateSetAnswerFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueInputBinding
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

        initView()

        return _binding.root
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_answer)
        _binding.executePendingBindings()
    }
}