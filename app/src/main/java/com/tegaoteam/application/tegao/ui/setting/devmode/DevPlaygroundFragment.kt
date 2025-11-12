package com.tegaoteam.application.tegao.ui.setting.devmode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentDevPlaygroundBinding

class DevPlaygroundFragment : Fragment() {
    private lateinit var _binding: FragmentDevPlaygroundBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dev_playground, container, false)

        // Inflate the layout for this fragment
        return _binding.root
    }
}