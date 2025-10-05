package com.tegaoteam.application.tegao.ui.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentLookupBinding

class LookupFragment : Fragment() {
    lateinit private var binding: FragmentLookupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_lookup, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
}