package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardManageQuickcrudListBinding
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.ui.learning.cardmanage.adapter.QuickCrudItemListAdapter
import com.tegaoteam.application.tegao.ui.learning.cardmanage.model.QuickCrudItemInfo
import com.tegaoteam.application.tegao.ui.shared.preset.DialogPreset
import com.tegaoteam.application.tegao.utils.setSrcWithResId

class CardManageGroupListFragment: Fragment() {
    private lateinit var _binding: FragmentCardManageQuickcrudListBinding
    private val _parentViewModel: CardManageActivityViewModel by activityViewModels()
    private lateinit var _adapter: QuickCrudItemListAdapter
    private lateinit var _navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardManageQuickcrudListBinding.inflate(inflater, container, false)

        initVariables()
        initObservers()
        initView()

        return _binding.root
    }

    private fun initVariables() {
        _adapter = QuickCrudItemListAdapter()
        _navController = findNavController()
    }

    private fun initObservers() {
        _parentViewModel.apply {
            cardGroups.observe(viewLifecycleOwner) { groups ->
                _adapter.submitList(groups.map { group ->
                    QuickCrudItemInfo(
                        id = group.groupId,
                        label = group.label,
                        quickInfo = _parentViewModel.fetchCardsOfGroupLiveData(group.groupId)
                            .map { getString(R.string.card_manage_card_count, it.size) },
                        onEditQabClickListener = { groupId ->
                            _navController.navigate(
                                CardManageGroupListFragmentDirections
                                    .actionCardManageGroupListFragmentToCardManageEditGroupFragment(
                                        groupId
                                    )
                            )
                        },
                        onDeleteQabClickListener = { groupId ->
                            DialogPreset.requestConfirmation(
                                context = requireContext(),
                                title = getString(
                                    R.string.card_manage_delete_group_title,
                                    group.label
                                ),
                                message = R.string.card_manage_delete_group_message,
                                lambdaRun = { _parentViewModel.deleteGroup(groupId) }
                            )
                        },
                        onItemClickListener = { groupId ->
                            _navController.navigate(
                                CardManageGroupListFragmentDirections
                                    .actionCardManageGroupListFragmentToCardManageCardListFragment(
                                        groupId
                                    )
                            )
                        },
                        lifecycleOwner = viewLifecycleOwner
                    )
                } )
                _binding.itemCountTxt.text = getString(R.string.card_manage_group_count, groups.size)
            }
        }
    }

    private fun initView() {
        _binding.loFragmentTitleTxt.setText(R.string.card_manage_groupList_label)
        _binding.quickCrudListRcy.adapter = _adapter
        _binding.placeholderQabBtn.apply {
            setSrcWithResId(R.drawable.ftc_round_plus_128)
            setOnClickListener {
                DialogPreset.requestValueDialog(
                    requireContext(),
                    R.string.card_create_add_group_label,
                    R.string.card_create_add_group_message
                ) { groupName ->
                    _parentViewModel.addNewCardGroup(groupName)
                }
            }
            visibility = View.VISIBLE
        }

        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.executePendingBindings()
    }
}