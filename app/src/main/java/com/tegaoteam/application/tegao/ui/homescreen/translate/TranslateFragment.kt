package com.tegaoteam.application.tegao.ui.homescreen.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentMainTranslateBinding
import com.tegaoteam.application.tegao.ui.homescreen.MainActivityViewModel
import kotlin.getValue

class TranslateFragment: Fragment() {
    private lateinit var _binding: FragmentMainTranslateBinding
    private val _parentViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainTranslateBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onResume() {
        _parentViewModel.fragmentChanged(R.id.main_translateFragment.toString())
        super.onResume()
    }
}