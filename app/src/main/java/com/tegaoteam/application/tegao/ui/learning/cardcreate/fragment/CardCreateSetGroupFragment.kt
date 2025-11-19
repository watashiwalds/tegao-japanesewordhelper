package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateSetGroupBinding
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel

class CardCreateSetGroupFragment : Fragment() {
    private lateinit var _binding: FragmentCardCreateSetGroupBinding
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_card_create_set_group, container, false)
        initObservers()
        return _binding.root
    }

    private fun initObservers() {
        _parentViewModel.cardContentMaterial.observe(viewLifecycleOwner) {
            _binding.rawDataDisplay.text = it.toString()
        }
    }
}