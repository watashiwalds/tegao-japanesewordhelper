package com.tegaoteam.application.tegao.ui.homescreen.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentMainTranslateBinding
import com.tegaoteam.application.tegao.ui.homescreen.MainActivityViewModel
import timber.log.Timber
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

        initView()

        return _binding.root
    }

    override fun onResume() {
        _parentViewModel.fragmentChanged(R.id.main_translateFragment.toString())

        super.onResume()
    }

    private fun initView() {
        val autofillTest = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>())
        autofillTest.addAll("JA", "VI", "EN")

        _binding.apply {
            translateFromCtx.apply {
                setAdapter(autofillTest)
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (focused) showDropDown() }
                onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 -> Timber.i("Change FROM language to ${autofillTest.getItem(position)}") }
            }
            translateToCtx.apply{
                setAdapter(autofillTest)
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (focused) showDropDown() }
                onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 -> Timber.i("Change TO language to ${autofillTest.getItem(position)}") }
            }
        }
    }
}