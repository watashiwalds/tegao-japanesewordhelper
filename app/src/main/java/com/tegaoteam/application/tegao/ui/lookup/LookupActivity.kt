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
import com.tegaoteam.application.tegao.databinding.ActivityLookupBinding
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.AppToast

class LookupActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLookupBinding
    private lateinit var _viewModel: LookupActivityViewModel

    private lateinit var _wordSearchResultListAdapter: WordDefinitionCardAdapter

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
        setupAdaptiveViews()

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
        _viewModel = ViewModelProvider(this).get(LookupActivityViewModel::class.java)
        _binding.viewModel = _viewModel

        _wordSearchResultListAdapter = WordDefinitionCardAdapter()
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
                AppToast.show(this, "Search clicked", AppToast.LENGTH_SHORT)
                _viewModel.searchKeyword()
            }
        }
        _viewModel.evChangeToWordMode.beacon.observe(this) {
            if (_viewModel.evChangeToWordMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.WORD)
            }
        }
        _viewModel.evChangeToKanjiMode.beacon.observe(this) {
            if (_viewModel.evChangeToKanjiMode.receive()) {
                GlobalState.setLookupMode(GlobalState.LookupMode.KANJI)
            }
        }
        _viewModel.searchResultList.observe(this) {
            updateSearchResultDisplay(it)
        }
    }

    fun updateSearchString() = _viewModel.setSearchString(_binding.keywordInputEdt.text.toString())

    fun clearSearchString() {
        _binding.keywordInputEdt.text.clear()
        updateSearchString()
    }

    fun displayDictionaryOptions() {
        val availableDictionaries = _viewModel.availableDictionariesList
        val dictChipAdapter = DictionaryChipsAdapter(this)
        dictChipAdapter.submitDictList(availableDictionaries) { dictId ->
            _viewModel.selectedDictionaryId = dictId
        }
        _binding.loDictionaryChipRcy.adapter = dictChipAdapter
    }

    fun setupAdaptiveViews() {
        when (GlobalState.lookupMode.value) {
            GlobalState.LookupMode.WORD -> _binding.loSearchResultCst.adapter = _wordSearchResultListAdapter
            GlobalState.LookupMode.KANJI -> {}
        }
    }

    fun updateSearchResultDisplay(list: List<Any>) {
        when (GlobalState.lookupMode.value) {
            GlobalState.LookupMode.WORD -> _wordSearchResultListAdapter.submitList(list as List<Word>)
            GlobalState.LookupMode.KANJI -> {}
        }
    }

    private fun makeStartState() {
        _binding.keywordInputEdt.requestFocus()
    }
}