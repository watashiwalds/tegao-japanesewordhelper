package com.tegaoteam.application.tegao.ui.learning.cardmanage

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ActivityCardManageBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment.CardManageCardListFragmentDirections
import com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment.CardManageGroupListFragmentDirections

class CardManageActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCardManageBinding
    private lateinit var _navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_manage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initVariables()

        setupHeaderBar()
        interpretStartIntent(intent)
    }

    private fun initVariables() {
        _navController = (supportFragmentManager.findFragmentById(R.id.cardManageFragmentContainerView) as NavHostFragment).navController
    }

    private fun setupHeaderBar() {
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            getString(R.string.card_manage_activity_label),
            { if (_navController.popBackStack()) finish() }
        )
    }

    private fun interpretStartIntent(intent: Intent) {
        val actionId = CardManageActivityGate.arriveIntentFrag(intent)
        val dataId = CardManageActivityGate.arriveIntentData(intent)
        when (actionId) {
            CardManageActivityGate.ACTION_CARDLIST -> _navController.navigate(CardManageGroupListFragmentDirections.actionCardManageGroupListFragmentToCardManageCardListFragment(dataId))
            CardManageActivityGate.ACTION_EDITGROUP -> _navController.navigate(CardManageGroupListFragmentDirections.actionCardManageGroupListFragmentToCardManageEditGroupFragment(dataId))
            CardManageActivityGate.ACTION_EDITCARD -> _navController.navigate(CardManageCardListFragmentDirections.actionCardManageCardListFragmentToCardManageEditCardFragment(dataId))
            else -> _navController.navigate(R.id.cardManageGroupListFragment)
        }
    }
}