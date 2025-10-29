package com.tegaoteam.application.tegao.ui.homescreen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.SettingHub
import com.tegaoteam.application.tegao.databinding.ActivityMainBinding
import com.tegaoteam.application.tegao.databinding.ItemNavbarIconNLabelChipBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.ui.shared.IdTranslator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavbar()
    }

    private val settingRepo = SettingHub()
    private fun setupBottomNavbar() {
        val navIds = settingRepo.getMainNavbarItemIds()
        val navItems = navIds.mapNotNull { id -> IdTranslator.mainNavbarId(id) }
        val navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        binding.navItemsRcy.layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.gridEven(this)
        val navAdapter = ThemedChipListAdapter(this, ItemNavbarIconNLabelChipBinding::inflate).apply {
            submitListWithClickListener(navItems) { fragmentString ->
                navController.navigate(fragmentString.toInt(), null, navOptions)
            }
        }
        binding.navItemsRcy.adapter = navAdapter
        binding.executePendingBindings()
    }
}