package com.tegaoteam.application.tegao.ui.homescreen.lookup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
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
import com.tegaoteam.application.tegao.ui.lookup.LookupActivity
import com.tegaoteam.application.tegao.ui.lookup.LookupActivityGate
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

        updateSearchHistoryDisplay()

        return _binding.root
    }

    private fun initVariables() {
        _searchHistoryRepo = SearchHistoryHub()

        _viewModel = ViewModelProvider(this, LookupFragmentViewModel.Companion.ViewModelFactory(_searchHistoryRepo)).get(LookupFragmentViewModel::class.java)
        _binding.lifecycleOwner = this
        _binding.viewModel = _viewModel

        _wordSearchHistoryAdapter = SearchHistoryListAdapter(ItemSearchhistoryWordBinding::inflate) { keyword ->
            Timber.i("Word history search $keyword")
            startActivity(LookupActivityGate.departIntent(requireContext(), keyword))
        }
        _kanjiSearchHistoryAdapter = SearchHistoryListAdapter(ItemSearchhistoryKanjiBinding::inflate) { keyword ->
            Timber.i("Kanji history search $keyword")
            startActivity(LookupActivityGate.departIntent(requireContext(), keyword))
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
            }
        }
        _viewModel.evChangeToKanjiMode.beacon.observe(viewLifecycleOwner) {
            if (_viewModel.evChangeToKanjiMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.KANJI)
            }
        }
        _viewModel.wordSearchHistories.observe(viewLifecycleOwner) { list ->
            Timber.i("WordHistory changed, size = ${list.size}")
            _wordSearchHistoryAdapter.submitList(list)
            if (_viewModel.lookupMode.value == GlobalState.LookupMode.WORD) updateSearchHistoryCount(list.size)
        }
        _viewModel.kanjiSearchHistories.observe(viewLifecycleOwner) { list ->
            Timber.i("KanjiHistory changed, size = ${list.size}")
            _kanjiSearchHistoryAdapter.submitList(list)
            if (_viewModel.lookupMode.value == GlobalState.LookupMode.KANJI) updateSearchHistoryCount(list.size)
        }
        _viewModel.lookupMode.observe(viewLifecycleOwner) { value ->
            updateSearchHistoryDisplay()
        }
    }

    private fun updateSearchHistoryDisplay() {
        Timber.i("Updating search history display according with mode ${_viewModel.lookupMode.value}")
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
            updateSearchHistoryCount(adapter?.itemCount?: 0)
        }
    }

    private fun updateSearchHistoryCount(count: Int) {
        _binding.loTitleHistoryTxt.text =
            if (count > 0) getString(R.string.title_search_history_count, count)
            else getString(R.string.title_search_history_empty)
    }

    private fun navigatingToLookup() {
        val anim = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.instant,
            R.anim.instant
        )
        startActivity(LookupActivityGate.departIntent(requireContext(), ""), anim.toBundle())
    }
}