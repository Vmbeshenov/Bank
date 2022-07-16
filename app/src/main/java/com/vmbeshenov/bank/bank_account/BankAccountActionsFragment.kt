package com.vmbeshenov.bank.bank_account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.vmbeshenov.bank.adapters.PaymentMenuTransactionsAdapter
import com.vmbeshenov.bank.databinding.FragmentBankAccountActionsBinding
import com.vmbeshenov.bank.payments.BetweenAccountsActivity
import com.vmbeshenov.bank.payments.TransactionsActivity

class BankAccountActionsFragment : Fragment(), PaymentMenuTransactionsAdapter.ListenerTransactions {

    private lateinit var binding: FragmentBankAccountActionsBinding
    private val adapterTransactions = PaymentMenuTransactionsAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentBankAccountActionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rcViewCardActions.layoutManager = GridLayoutManager(activity, 2)
            rcViewCardActions.adapter = adapterTransactions
        }
    }

    override fun onClick(itemMenuTransaction: PaymentMenuTransactionsAdapter.PaymentMenuTransactions) {
        when (itemMenuTransaction.title){
            PaymentMenuTransactionsAdapter.PaymentMenuTransactions.payment_menu_items_transactions[0].title -> {
                val intent = Intent(this.context, BetweenAccountsActivity::class.java)
                startActivity(intent)
            }
            PaymentMenuTransactionsAdapter.PaymentMenuTransactions.payment_menu_items_transactions[1].title -> {
                val intent = Intent(this.context, TransactionsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}