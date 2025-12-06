package com.tegaoteam.application.tegao.ui.homescreen

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SettingHub
import com.tegaoteam.application.tegao.databinding.ActivityMainBinding
import com.tegaoteam.application.tegao.databinding.ItemChipNavbarIconNLabelBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.setting.SettingActivity
import com.tegaoteam.application.tegao.ui.shared.BehaviorPreset
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.ui.shared.IdTranslator

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _viewModel = ViewModelProvider(this)[MainActivityViewModel::class]

        setupBottomNavbar()
        bindOptionButton()
    }

    private val settingRepo = SettingHub()
    private fun setupBottomNavbar() {
        val navIds = settingRepo.getMainNavbarItemIds()
        val navItems = navIds.mapNotNull { id -> IdTranslator.mainNavbarId(id) }
        val navController = (supportFragmentManager.findFragmentById(R.id.mainActFragmentContainerView) as NavHostFragment).navController
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        _binding.navItemsRcy.layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.gridEven(this)
        val navAdapter = ThemedChipListAdapter(this, ItemChipNavbarIconNLabelBinding::inflate).apply {
            themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
            submitList(navItems)
            themedChipManager?.apply {
                setChipsOnSelectedListener { tabChip ->
                    navController.navigate(tabChip.id.toInt(), null, navOptions)
                }
                selectFirst()
            }
        }
        _binding.navItemsRcy.adapter = navAdapter

        _viewModel.fragmentChangeId.observe(this) {
            val matched = navItems.find { item -> item.id == it }
            matched?.let { m ->
                if (m.isSelected.value != true) m.nowSelected()
            }
        }

        _binding.executePendingBindings()
    }

    private fun bindOptionButton() {
        _binding.optionMenuBtn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus is EditText) BehaviorPreset.cancelInputWhenTouchOutside(
            motionEvent = ev,
            inputView = currentFocus!!,
            focusedView = currentFocus,
            imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager,
            currentFocus, findViewById(R.id.unv_customInputHolder_frm), findViewById(R.id.switch_handwritingMode_icl)
        )
        return super.dispatchTouchEvent(ev)
    }
}