package com.tegaoteam.application.tegao.ui.learning.cardlearn.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionConfigureBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarInfo

class LearningSessionConfigureFragment : Fragment() {
    private lateinit var _binding: FragmentLearningSessionConfigureBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearningSessionConfigureBinding.inflate(layoutInflater, container, false)

        initView()

        return _binding.root
    }

    private fun initView() {
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            label = getString(R.string.card_learn_config_headerTitle),
            backOnClickListener = { requireActivity().finish() }
        )
    }
}