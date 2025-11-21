package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueSelectBinding
import com.tegaoteam.application.tegao.databinding.ItemChipCheckboxTextBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset

class CardCreateSetGroupFragment : Fragment() {
    private lateinit var _binding: FragmentCardCreateValueSelectBinding
    private lateinit var _adapter: ThemedChipListAdapter<ItemChipCheckboxTextBinding>
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
        _binding.lifecycleOwner = this
        _adapter = ThemedChipListAdapter(this, ItemChipCheckboxTextBinding::inflate).apply {
            themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
        }
        _binding.loSelectableGroupListRcy.adapter = _adapter
        _binding.executePendingBindings()
    }

    private fun initObservers() {
        _parentViewModel.cardGroups.observe(viewLifecycleOwner) {
            _adapter.submitList(it.map { (groupId, label) -> ThemedChipItem(groupId.toString(), label, MutableLiveData<Boolean>()) })
            displayLastResult()
        }
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_group)
        _binding.qabNewGroupBtn.apply {
            setOnClickListener {
                DialogPreset.requestValueDialog(
                    requireActivity(),
                    R.string.card_create_add_group_label,
                    R.string.card_create_add_group_message
                ) { groupName ->
                    _parentViewModel.addNewCardGroup(groupName)
                }
            }
            visibility = View.VISIBLE
        }
        _binding.nextBtn.setOnClickListener {
            val selected = _adapter.themedChipManager?.selectedChips?.map { it.id.toLong() }?.toList()?: listOf()
            if (selected.isEmpty())
                DialogPreset.requestConfirmation(
                    requireActivity(),
                    message = R.string.card_create_error_no_group
                )
            else {
                _parentViewModel.submitSelectedCardGroups(_adapter.themedChipManager?.selectedChips?.map { it.id.toLong() }?.toList() ?: listOf())
                findNavController().navigate(CardCreateSetGroupFragmentDirections.actionCardCreateSetGroupFragmentToCardCreateSetTypeFragment())
            }
        }
    }

    private fun displayLastResult() {
        val selected = _parentViewModel.selectedGroupIds.map { it.toString() }
        if (selected.isEmpty()) return
        _adapter.themedChipManager?.chips?.forEach { if (it.id in selected) it.nowSelected() }
    }
}