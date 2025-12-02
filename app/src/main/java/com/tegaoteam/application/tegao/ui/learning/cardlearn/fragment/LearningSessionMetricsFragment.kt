package com.tegaoteam.application.tegao.ui.learning.cardlearn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tegaoteam.application.tegao.databinding.FragmentLearningSessionMetricsBinding

class LearningSessionMetricsFragment: Fragment() {
    private lateinit var _binding: FragmentLearningSessionMetricsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearningSessionMetricsBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }
}