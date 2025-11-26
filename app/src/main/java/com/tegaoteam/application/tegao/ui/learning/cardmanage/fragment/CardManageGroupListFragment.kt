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
import com.tegaoteam.application.tegao.ui.homescreen.learning.LearningInfoDataClasses
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.ui.learning.cardmanage.adapter.QuickCrudItemListAdapter
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
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
            stateDeleteGroup.observe(viewLifecycleOwner) {
                DialogPreset.apply {
                    dismissCurrentSnackbar()
                    when (it) {
                        CardManageActivityViewModel.STATUS_PROCESSING -> showSnackbar(_binding.root, R.string.phrase_processing)
                        CardManageActivityViewModel.STATUS_SUCCESS -> AppToast.show(R.string.card_manage_delete_success, AppToast.LENGTH_SHORT)
                        CardManageActivityViewModel.STATUS_FAILURE -> AppToast.show(R.string.card_manage_delete_failed, AppToast.LENGTH_SHORT)
                    }
                }
            }
            cardGroups.observe(viewLifecycleOwner) { groups ->
                _adapter.submitList(groups.map { group ->
                    val editGroupLambda = { groupId: Long -> _navController.navigate(CardManageGroupListFragmentDirections.actionCardManageGroupListFragmentToCardManageEditGroupFragment(groupId)) }
                    LearningInfoDataClasses.QuickCrudItemInfo(
                        id = group.groupId,
                        label = group.label,
                        quickInfo = _parentViewModel.fetchCardsOfGroupLiveData(group.groupId).map { getString(R.string.card_manage_group_size_count, it.size.toString()) },
                        onEditQabClickListener = { groupId -> editGroupLambda(groupId) },
                        onDeleteQabClickListener = { groupId ->
                            DialogPreset.requestConfirmation(
                                context = requireContext(),
                                title = getString(R.string.card_manage_delete_group_title, group.label),
                                message = R.string.card_manage_delete_group_message,
                                lambdaRun = { _parentViewModel.deleteGroup(groupId) }
                            )
                        },
                        onItemClickListener = { groupId -> editGroupLambda(groupId) },
                        lifecycleOwner = viewLifecycleOwner
                    )
                } )
            }
        }
    }

    private fun initView() {
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