package com.tegaoteam.application.tegao.ui.learning.cardsharing

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SharingHub
import com.tegaoteam.application.tegao.databinding.ActivityCardSharingBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.utils.AppToast

class CardSharingActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCardSharingBinding
    private lateinit var _viewModel: CardSharingViewModel
    private val _sharingHub = SharingHub.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_sharing)
        _viewModel = ViewModelProvider(this, CardSharingViewModel.Companion.ViewModelFactory(_sharingHub))[CardSharingViewModel::class.java]

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
        setupCarddecks()
        setupCardpacks()
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
    private fun setupCarddecks() {
        _deckRcyAdapter = CardDeckListAdapter { deck -> AppToast.show(deck.label, AppToast.LENGTH_SHORT) }
        _binding.packListRcy.adapter = _deckRcyAdapter

        _viewModel.fetchedPack.observe(this) {
            _deckRcyAdapter.submitList(it)
        }
    }
}