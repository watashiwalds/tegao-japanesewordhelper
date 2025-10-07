package com.tegaoteam.application.tegao.ui.homescreen.lookup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentLookupBinding

class LookupFragment : Fragment() {
    private lateinit var _binding: FragmentLookupBinding
    private lateinit var _viewModel: LookupFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_lookup, container, false)

        initVariables()
        initObservers()

        return _binding.root
    }

    private fun initVariables() {
        _viewModel = ViewModelProvider(this).get(LookupFragmentViewModel::class.java)
        _binding.lifecycleOwner = this
        _binding.viewModel = _viewModel
    }

    private fun initObservers() {
        _viewModel.evNavigateToLookupActivity.beacon.observe(viewLifecycleOwner) {
            if (_viewModel.evNavigateToLookupActivity.receive()) {
                navigatingToLookup()
            }
        }
    }

    private fun navigatingToLookup() {
        findNavController().navigate(LookupFragmentDirections.actionLookupFragmentToLookupActivity())
    }
}