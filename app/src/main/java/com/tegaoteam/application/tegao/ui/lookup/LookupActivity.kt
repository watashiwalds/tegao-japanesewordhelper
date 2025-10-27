package com.tegaoteam.application.tegao.ui.lookup

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.hub.ConfigHub
import com.tegaoteam.application.tegao.data.hub.DictionaryHub
import com.tegaoteam.application.tegao.data.hub.SearchHistoryHub
import com.tegaoteam.application.tegao.databinding.ActivityLookupBinding
import com.tegaoteam.application.tegao.databinding.ItemOptionOnlyChipBinding
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.repo.ConfigRepo
import com.tegaoteam.application.tegao.domain.repo.DictionaryRepo
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.ui.component.searchdisplay.KanjisDefinitionWidgetRecyclerAdapter
import com.tegaoteam.application.tegao.ui.component.searchdisplay.WordDefinitionCardListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.toggleVisibility

class LookupActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLookupBinding
    private lateinit var _viewModel: LookupActivityViewModel

    private lateinit var _wordSearchResultAdapter: WordDefinitionCardListAdapter
    private lateinit var _kanjiSearchResultAdapter: KanjisDefinitionWidgetRecyclerAdapter

    private lateinit var _dictionaryRepo: DictionaryRepo
    private lateinit var _configRepo: ConfigRepo
    private lateinit var _searchHistoryRepo: SearchHistoryRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_lookup)
        _binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(_binding.beforeMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initVariables()
        initListeners()
        initObservers()

        displayDictionaryOptions()
        updateSearchResultAdapter()

        makeStartState()
    }

    // UX: User click outside the input box -> Input box lose focus
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusedView = currentFocus
        if (focusedView != null && ev?.action == MotionEvent.ACTION_DOWN) {
            val focusRect = Rect()
            focusedView.getGlobalVisibleRect(focusRect)
            if (!focusRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                focusedView.clearFocus()

                // Hide virtual keyboard 'cause Android don't do it automatically
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)

            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun initVariables() {
        _configRepo = ConfigHub()
        _dictionaryRepo = DictionaryHub(_configRepo)
        _searchHistoryRepo = SearchHistoryHub()

        _viewModel = ViewModelProvider(this, LookupActivityViewModel.Companion.ViewModelFactory(_dictionaryRepo, _configRepo, _searchHistoryRepo))[LookupActivityViewModel::class.java]
        _binding.viewModel = _viewModel

        _wordSearchResultAdapter = WordDefinitionCardListAdapter(this)
        _kanjiSearchResultAdapter = KanjisDefinitionWidgetRecyclerAdapter(this) { kanjiId ->
            _viewModel.logSearch(kanjiId)
        }
    }

    private fun initListeners() {
        _binding.keywordInputEdt.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                updateSearchString()
            }
        }
    }

    private fun initObservers() {
        _viewModel.evClearSearchString.beacon.observe(this) {
            if (_viewModel.evClearSearchString.receive()) {
                clearSearchString()
            }
        }
        _viewModel.evStartSearch.beacon.observe(this) {
            if (_viewModel.evStartSearch.receive()) {
                clearSearchResult()
                _viewModel.searchKeyword()
            }
        }
        _viewModel.evChangeToWordMode.beacon.observe(this) {
            if (_viewModel.evChangeToWordMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.WORD)
                updateSearchResultAdapter()
            }
        }
        _viewModel.evChangeToKanjiMode.beacon.observe(this) {
            if (_viewModel.evChangeToKanjiMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.KANJI)
                updateSearchResultAdapter()
            }
        }
        _viewModel.searchResultList.observe(this) {
            updateSearchResultValue(it)
        }
    }

    fun updateSearchString() = _viewModel.setSearchString(_binding.keywordInputEdt.text.toString())

    fun clearSearchString() {
        _binding.keywordInputEdt.text.clear()
        updateSearchString()
    }

    fun displayDictionaryOptions() {
        val availableDictionaries = _viewModel.availableDictionariesList
        val dictChipAdapter = ThemedChipListAdapter(this, ItemOptionOnlyChipBinding::inflate)
        dictChipAdapter.submitListWithClickListener(availableDictionaries.map{ ThemedChipItem.fromDictionary(it) }) { dictId ->
            _viewModel.selectedDictionaryId = dictId
            _viewModel.evStartSearch.ignite()
        }
        _binding.loDictionaryChipRcy.addItemDecoration(DisplayHelper.LinearDividerItemDecoration.make(
            0,
            TegaoApplication.instance.applicationContext.resources.getDimensionPixelSize(R.dimen.padding_tiny)))
        _binding.loDictionaryChipRcy.adapter = dictChipAdapter
    }

    private fun clearSearchResult() {
        when (_viewModel.lookupMode.value) {
            GlobalState.LookupMode.WORD -> _wordSearchResultAdapter.submitList(listOf())
            GlobalState.LookupMode.KANJI -> _kanjiSearchResultAdapter.submitList(listOf())
        }
    }

    fun updateSearchResultAdapter() {
        val toAdapter = when (_viewModel.lookupMode.value) {
            GlobalState.LookupMode.WORD -> _wordSearchResultAdapter
            GlobalState.LookupMode.KANJI -> _kanjiSearchResultAdapter
        }
        _binding.loSearchResultCst.apply {
            if (adapter != toAdapter) {
                adapter = null
                recycledViewPool.clear()
                adapter = toAdapter
                requestLayout()
            }
            toggleVisibility(false)
        }
        _viewModel.evStartSearch.ignite()
        //for testing
        _viewModel.evIsRcyAdapterAvailable.value = _binding.loSearchResultCst.adapter != null
    }

    fun updateSearchResultValue(list: List<Any>) {
        _binding.loSearchResultCst.toggleVisibility(true)
        @Suppress("unchecked_cast")
        when (_viewModel.lookupMode.value) {
            GlobalState.LookupMode.WORD -> {
                _wordSearchResultAdapter.submitList(list as List<Word>)
                _viewModel.logSearch(_viewModel.userSearchString.value!!)
            }
            GlobalState.LookupMode.KANJI -> _kanjiSearchResultAdapter.submitList(list as List<Kanji>)
        }
    }

    private fun makeStartState() {
        _binding.keywordInputEdt.requestFocus()
    }
}