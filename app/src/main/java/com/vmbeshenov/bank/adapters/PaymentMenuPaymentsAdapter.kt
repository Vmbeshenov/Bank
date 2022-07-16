package com.vmbeshenov.bank.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.databinding.ItemPaymentPagePaymentsBinding

class PaymentMenuPaymentsAdapter: RecyclerView.Adapter<PaymentMenuPaymentsAdapter.PaymentsHolder>() {

    class PaymentsHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = ItemPaymentPagePaymentsBinding.bind(item)
        fun bind(payment_items: PaymentMenuPayments) = with(binding){
            titlePaymentMenuItem.text = payment_items.title
            imagePaymentMenuItem.setImageResource(payment_items.iconId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_page_payments, parent, false)
        return PaymentsHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentsHolder, position: Int) {
        holder.bind(PaymentMenuPayments.payment_menu_item_payments[position])
    }

    override fun getItemCount(): Int {
        return PaymentMenuPayments.payment_menu_item_payments.size
    }

    data class PaymentMenuPayments(val title: String, val iconId: Int){
        companion object {
            @JvmField
            val payment_menu_item_payments = arrayOf(
                PaymentMenuPayments("Мобильная связь", R.drawable.ic_phone),
                PaymentMenuPayments("По реквизитам", R.drawable.ic_account_details)
            )
        }
    }
}