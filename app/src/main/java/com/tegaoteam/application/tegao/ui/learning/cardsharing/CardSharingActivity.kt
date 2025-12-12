package com.tegaoteam.application.tegao.ui.learning.cardsharing

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.data.hub.SharingHub
import com.tegaoteam.application.tegao.databinding.ActivityCardSharingBinding
import com.tegaoteam.application.tegao.databinding.DialogCardSharingCarddeckDetailsBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.ui.shared.preset.DialogPreset
import com.tegaoteam.application.tegao.utils.AppToast

class CardSharingActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCardSharingBinding
    private lateinit var _viewModel: CardSharingViewModel
    private val _sharingHub = SharingHub.instance
    private val _learningRepo = LearningHub()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_sharing)
        _viewModel = ViewModelProvider(this, CardSharingViewModel.Companion.ViewModelFactory(_sharingHub, _learningRepo))[CardSharingViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        HeaderBarBindingHelper.bind(
            headerBinding = _binding.loHeaderBarIcl,
            label = getString(R.string.card_sharing_headerLabel),
            backOnClickListener = { finish() }
        )
        _viewModel.evFetchFailed.apply {
            beacon.observe(this@CardSharingActivity) {
                if (receive()) {
                    getMessage()?.let { AppToast.show(it, AppToast.LENGTH_SHORT) }
                }
            }
        }
        _viewModel.evImportFinished.apply {
            beacon.observe(this@CardSharingActivity) {
                if (receive()) {
                    DialogPreset.cancelCurrentProcessingDialog()
                    getMessage()?.let {
                        AppToast.show(getString(R.string.card_sharing_importingDeck_partFail, it), AppToast.LENGTH_SHORT)
                    }?: AppToast.show(R.string.card_sharing_importingDeck_success, AppToast.LENGTH_SHORT)
                }
            }
        }
        setupCarddecksListing()
        setupCardpacks()
        setupCarddeckDetails()
    }

    private lateinit var _sourceAutoFillAdapter: ArrayAdapter<String>
    private fun setupCardpacks() {
        val sources = _viewModel.packSources

        val autoFill = sources.map { it.packName.ifBlank { it.link } }
        if (!::_sourceAutoFillAdapter.isInitialized) _sourceAutoFillAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        _sourceAutoFillAdapter.apply {
            clear()
            addAll(autoFill)
        }
        _binding.sourceNameTxt.apply {
            setAdapter(_sourceAutoFillAdapter)
            setOnClickListener { showDropDown() }
            onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 ->
                _viewModel.fetchCardpackContents(sources[position])
            }
            post {
                setText(adapter.getItem(0).toString(), false)
                _viewModel.fetchCardpackContents(sources[0])
            }
        }
    }

    private lateinit var _deckRcyAdapter: CardDeckListAdapter
    private fun setupCarddecksListing() {
        _deckRcyAdapter = CardDeckListAdapter { deck ->
            _viewModel.fetchCarddeckContent(deck)
            DialogPreset.quickView(
                context = this,
                view = _deckDialogBinding.root,
                message = getString(R.string.card_sharing_deckFrom_message, _binding.sourceNameTxt.text)
            )
        }
        _binding.packListRcy.adapter = _deckRcyAdapter

        _viewModel.fetchedPack.observe(this) {
            _deckRcyAdapter.submitList(it)
        }
    }

    private lateinit var _deckDialogBinding: DialogCardSharingCarddeckDetailsBinding
    private fun setupCarddeckDetails() {
        if (!::_deckDialogBinding.isInitialized) {
            _deckDialogBinding = DialogCardSharingCarddeckDetailsBinding.inflate(layoutInflater)
            _deckDialogBinding.apply {
                cancelBtn.setOnClickListener { findViewById<View>(R.id.dismiss_btn)?.callOnClick() }
                importBtn.setOnClickListener { performImportDeck(deckInfo?.parsedCards, deckInfo?.label) }
            }
        }
        _viewModel.fetchedDeck.observe(this) {
            _deckDialogBinding.deckInfo = it
            _deckDialogBinding.executePendingBindings()
        }
    }

    private fun performImportDeck(cards: List<CardEntry>?, defaultGroupLabel: String? = null) {
        if (cards == null) return
        val confirmImport = { selectedLabel: String ->
            displayImportProgress(cards.size.toString())
            _viewModel.importCardDeck(
                CardGroup(groupId = 0, label = selectedLabel),
                cards
            )
        }
        DialogPreset.requestValueDialog(
            context = this,
            title = R.string.card_sharing_importingDeck_confirmGroupLabel_title,
            message = R.string.card_sharing_importingDeck_confirmGroupLabel_message,
            defaultValue = defaultGroupLabel,
            lambdaRun = { label -> confirmImport(label) }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun displayImportProgress(finValue: String) {
        val updateText = DialogPreset.processing(
            context = this,
            message = R.string.card_sharing_importingDeck_inProgress
        )
        _viewModel.importProgress.apply {
            removeObservers(this@CardSharingActivity)
            observe(this@CardSharingActivity) { updateText.text = "($it/$finValue)" }
        }
    }
}