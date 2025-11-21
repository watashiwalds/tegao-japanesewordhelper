package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueSelectBinding
import com.tegaoteam.application.tegao.databinding.ItemChipCheckboxTextBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import kotlin.getValue

class CardCreateSetTypeFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueSelectBinding
    private lateinit var _adapter: ThemedChipListAdapter<ItemChipCheckboxTextBinding>
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_card_create_value_select, container, false)

        initVariables()
//        initObservers()
//        initView()
//
//        displayLastResult()

        return _binding.root
    }

    private fun initVariables() {
        _binding.lifecycleOwner = this
        _adapter = ThemedChipListAdapter(
            lifecycleOwner = viewLifecycleOwner,
            bindingInflater = ItemChipCheckboxTextBinding::inflate
        ).apply {
            themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
        }
        _binding.executePendingBindings()
    }
}