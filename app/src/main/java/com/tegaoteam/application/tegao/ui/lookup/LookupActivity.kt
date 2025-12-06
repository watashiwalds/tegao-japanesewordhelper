package com.tegaoteam.application.tegao.ui.lookup

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
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.data.hub.DictionaryHub
import com.tegaoteam.application.tegao.data.hub.SearchHistoryHub
import com.tegaoteam.application.tegao.data.hub.SettingHub
import com.tegaoteam.application.tegao.databinding.ActivityLookupBinding
import com.tegaoteam.application.tegao.databinding.ItemChipDictionaryPickBinding
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.repo.AddonRepo
import com.tegaoteam.application.tegao.domain.repo.DictionaryRepo
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.component.handwriting.WritingViewBindingHelper
import com.tegaoteam.application.tegao.ui.component.searchdisplay.KanjisDefinitionWidgetRecyclerAdapter
import com.tegaoteam.application.tegao.ui.component.searchdisplay.WordDefinitionCardListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.shared.BehaviorPreset
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class LookupActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLookupBinding
    private lateinit var _viewModel: LookupActivityViewModel

    private lateinit var _wordSearchResultAdapter: WordDefinitionCardListAdapter
    private lateinit var _kanjiSearchResultAdapter: KanjisDefinitionWidgetRecyclerAdapter

    private lateinit var _dictionaryRepo: DictionaryRepo
    private lateinit var _searchHistoryRepo: SearchHistoryRepo
    private lateinit var _addonRepo: AddonRepo

    private var _handwritingBoardView: View? = null

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
        initAddons()

        displayDictionaryOptions()
        updateSearchResultAdapter()

        getActivityLaunchArgument().let {
            if (it.isNullOrBlank())
                makeCleanStartState()
            else
                makeRequestedSearchState(it)
        }
    }

    private fun initVariables() {
        _dictionaryRepo = DictionaryHub()
        _searchHistoryRepo = SearchHistoryHub()
        _addonRepo = AddonHub()

        _viewModel = ViewModelProvider(this, LookupActivityViewModel.Companion.ViewModelFactory(_dictionaryRepo, _searchHistoryRepo))[LookupActivityViewModel::class.java]
        _binding.viewModel = _viewModel

        _wordSearchResultAdapter = WordDefinitionCardListAdapter(this)
        _kanjiSearchResultAdapter = KanjisDefinitionWidgetRecyclerAdapter(this) { kanjiId ->
            _viewModel.logSearch(kanjiId)
        }
    }

    private fun initListeners() {
        _binding.unvInputFieldEdt.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                updateSearchString()
            }
        }
    }

    private fun initObservers() {
        _viewModel.userSearchString.observe(this) { value ->
            _binding.unvInputFieldEdt.setText(value)
        }
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
        _viewModel.nonResult.observe(this) {
            AppToast.show(it, AppToast.LENGTH_SHORT)
        }
    }

    private fun initAddons() {
        if (_viewModel.isHandwritingEnabled.value) _handwritingBoardView = WritingViewBindingHelper.fullSuggestionBoard(
            _addonRepo,
            this,
            _binding.unvInputFieldEdt,
            _binding.unvCustomInputHolderFrm,
            _binding.switchHandwritingModeIcl
        )
    }

    fun updateSearchString() = _viewModel.setSearchString(_binding.unvInputFieldEdt.text.toString())

    fun clearSearchString() {
        _binding.unvInputFieldEdt.text?.clear()
        updateSearchString()
    }

    fun displayDictionaryOptions() {
        val availableDictionaries = _viewModel.availableDictionariesList
        val dictChipAdapter = ThemedChipListAdapter(this, ItemChipDictionaryPickBinding::inflate)
        dictChipAdapter.themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
        dictChipAdapter.submitList(availableDictionaries.map{ ThemedChipItem.fromDictionary(it) })
        dictChipAdapter.themedChipManager?.apply {
            setChipsOnSelectedListener { dictChip ->
                _viewModel.selectedDictionaryId = dictChip.id
                _viewModel.evStartSearch.ignite()
            }
            selectFirst()
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

    private fun getActivityLaunchArgument(): String? {
        return LookupActivityGate.arriveIntent(intent)
    }

    private fun makeCleanStartState() {
        _binding.unvInputFieldEdt.requestFocus()
    }
    private fun makeRequestedSearchState(keyword: String) {
        _binding.unvInputFieldEdt.setText(keyword)
        updateSearchString()
        _viewModel.evStartSearch.ignite()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        BehaviorPreset.cancelInputWhenTouchOutside(
            ev,
            _binding.unvInputFieldEdt,
            currentFocus,
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager,
            _binding.inputClearBtn, _handwritingBoardView, _binding.switchHandwritingModeIcl.root
        )

        return super.dispatchTouchEvent(ev)
    }

}