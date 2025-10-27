package com.tegaoteam.application.tegao.ui.homescreen.lookup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SearchHistoryHub
import com.tegaoteam.application.tegao.databinding.FragmentLookupBinding
import com.tegaoteam.application.tegao.databinding.ItemSearchhistoryKanjiBinding
import com.tegaoteam.application.tegao.databinding.ItemSearchhistoryWordBinding
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.ui.homescreen.lookup.searchhistory.SearchHistoryListAdapter
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class LookupFragment : Fragment() {
    private lateinit var _binding: FragmentLookupBinding
    private lateinit var _viewModel: LookupFragmentViewModel

    private lateinit var _searchHistoryRepo: SearchHistoryRepo
    private lateinit var _wordSearchHistoryAdapter: SearchHistoryListAdapter<ItemSearchhistoryWordBinding>
    private lateinit var _searchHistoryLinearLayoutManager: LinearLayoutManager
    private lateinit var _kanjiSearchHistoryAdapter: SearchHistoryListAdapter<ItemSearchhistoryKanjiBinding>
    private lateinit var _searchHistoryGridLayoutManager: FlexboxLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_lookup, container, false)

        initVariables()
        initObservers()

        updateSearchHistoryAdapter()

        return _binding.root
    }

    private fun initVariables() {
        _searchHistoryRepo = SearchHistoryHub()

        _viewModel = ViewModelProvider(this, LookupFragmentViewModel.Companion.ViewModelFactory(_searchHistoryRepo)).get(LookupFragmentViewModel::class.java)
        _binding.lifecycleOwner = this
        _binding.viewModel = _viewModel

        _wordSearchHistoryAdapter = SearchHistoryListAdapter(ItemSearchhistoryWordBinding::inflate) { keyword ->
            Timber.i("Word history search $keyword")
        }
        _kanjiSearchHistoryAdapter = SearchHistoryListAdapter(ItemSearchhistoryKanjiBinding::inflate) { keyword ->
            Timber.i("Kanji history search $keyword")
        }

        _searchHistoryLinearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        _searchHistoryGridLayoutManager = DisplayHelper.FlexboxLayoutManagerMaker.gridEven(context?: requireContext())
    }

    private fun initObservers() {
        _viewModel.evNavigateToLookupActivity.beacon.observe(viewLifecycleOwner) {
            if (_viewModel.evNavigateToLookupActivity.receive()) {
                navigatingToLookup()
            }
        }
        _viewModel.evChangeToWordMode.beacon.observe(viewLifecycleOwner) {
            if (_viewModel.evChangeToWordMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.WORD)
                updateSearchHistoryAdapter()
            }
        }
        _viewModel.evChangeToKanjiMode.beacon.observe(viewLifecycleOwner) {
            if (_viewModel.evChangeToKanjiMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.KANJI)
                updateSearchHistoryAdapter()
            }
        }
        _viewModel.wordSearchHistories.observe(viewLifecycleOwner) { list ->
            Timber.i("WordHistory changed, size = ${list.size}")
            _wordSearchHistoryAdapter.submitList(list)
        }
        _viewModel.kanjiSearchHistories.observe(viewLifecycleOwner) { list ->
            Timber.i("KanjiHistory changed, size = ${list.size}")
            _kanjiSearchHistoryAdapter.submitList(list)
        }
    }

    private fun updateSearchHistoryAdapter() {
        Timber.i("Updating historyAdapter according with mode ${_viewModel.lookupMode.value}")
        val toAdapter = when (_viewModel.lookupMode.value) {
            GlobalState.LookupMode.WORD -> _wordSearchHistoryAdapter
            GlobalState.LookupMode.KANJI -> _kanjiSearchHistoryAdapter
        }
        _binding.historyListRcy.apply {
            toggleVisibility(false)
            layoutManager = when (_viewModel.lookupMode.value) {
                GlobalState.LookupMode.WORD -> _searchHistoryLinearLayoutManager
                GlobalState.LookupMode.KANJI -> _searchHistoryGridLayoutManager
            }
            if (adapter != toAdapter) {
                adapter = null
                recycledViewPool.clear()
                adapter = toAdapter
                requestLayout()
            }
            toggleVisibility(true)
        }
    }

    private fun navigatingToLookup() {
        findNavController().navigate(LookupFragmentDirections.actionLookupFragmentToLookupActivity())
    }
}