package com.vmbeshenov.bank.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.authorization.AuthorizationActivity
import com.vmbeshenov.bank.data.CardAccount
import com.vmbeshenov.bank.data.Constants
import com.vmbeshenov.bank.data.OperationsHistory
import com.vmbeshenov.bank.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.mainToolbar.setNavigationIcon(R.drawable.ic_account)

        binding.mainBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_item -> {
                    supportFragmentManager.beginTransaction().replace(binding.mainFragmentContainer.id, MainPageFragment()).commit()
                    true
                }
                R.id.payment_item -> {
                    supportFragmentManager.beginTransaction().replace(binding.mainFragmentContainer.id, PaymentsFragment()).commit()
                    true
                }
                R.id.history_item -> {
                    supportFragmentManager.beginTransaction().replace(binding.mainFragmentContainer.id, HistoryFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_item_exit -> {
                Firebase.auth.signOut()

                val listCards: MutableList<CardAccount> = CardAccount.cardAccounts.toMutableList()
                listCards.clear()
                CardAccount.cardAccounts = listCards.toTypedArray()

                val listHistory: MutableList<OperationsHistory> = OperationsHistory.operations.toMutableList()
                listHistory.clear()
                OperationsHistory.operations = listHistory.toTypedArray()

                Constants.constant.isReversed = false

                val intent = Intent(this, AuthorizationActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}