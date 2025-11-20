package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueSelectBinding
import com.tegaoteam.application.tegao.databinding.ItemChipCheckboxTextBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.SingleSelectThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.utils.QuickPreset

class CardCreateSetGroupFragment : Fragment() {
    private lateinit var _binding: FragmentCardCreateValueSelectBinding
    private val _adapter = SingleSelectThemedChipListAdapter(this, ItemChipCheckboxTextBinding::inflate)
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_card_create_value_select, container, false)

        initVariables()
        initObservers()
        initView()

        return _binding.root
    }

    private fun initVariables() {
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.loSelectableGroupListRcy.adapter = _adapter
        _binding.executePendingBindings()
    }

    private fun initObservers() {
        _parentViewModel.cardGroups.observe(viewLifecycleOwner) {
            _adapter.submitListWithClickListener(
                it.map { (groupId, label) -> ThemedChipItem(groupId.toString(), label, MutableLiveData<Boolean>()) },
                {}
            )
        }
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_group)
        _binding.qabNewGroupBtn.setOnClickListener {
            QuickPreset.requestValueDialog(
                requireActivity(),
                R.string.card_create_add_group_label,
                R.string.card_create_add_group_message
            ) { groupName ->
                _parentViewModel.addNewCardGroup(groupName)
            }
        }
    }
}