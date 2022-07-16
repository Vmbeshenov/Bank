package com.vmbeshenov.bank.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmbeshenov.bank.adapters.PaymentMenuPaymentsAdapter
import com.vmbeshenov.bank.adapters.PaymentMenuTransactionsAdapter
import com.vmbeshenov.bank.databinding.FragmentPaymentsBinding
import com.vmbeshenov.bank.payments.BetweenAccountsActivity
import com.vmbeshenov.bank.payments.TransactionsActivity

class PaymentsFragment : Fragment(), PaymentMenuTransactionsAdapter.ListenerTransactions {

    private lateinit var binding: FragmentPaymentsBinding
    private val adapterTransactions = PaymentMenuTransactionsAdapter(this)
    private val adapterPayments = PaymentMenuPaymentsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPaymentsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recyclerPaymentTransactions.layoutManager = GridLayoutManager(activity, 2)
            recyclerPaymentTransactions.adapter = adapterTransactions

            recyclerPaymentPayments.layoutManager = LinearLayoutManager(activity)
            recyclerPaymentPayments.adapter = adapterPayments
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