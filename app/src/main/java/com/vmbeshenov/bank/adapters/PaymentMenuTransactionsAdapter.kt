package com.vmbeshenov.bank.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.databinding.ItemPaymentPageTransactionsBinding

class PaymentMenuTransactionsAdapter(private val listener: ListenerTransactions): RecyclerView.Adapter<PaymentMenuTransactionsAdapter.PaymentMenuHolder>(){

    class PaymentMenuHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = ItemPaymentPageTransactionsBinding.bind(item)
        fun bind(payment_items: PaymentMenuTransactions, listener: ListenerTransactions) = with(binding){
            titlePaymentItem.text = payment_items.title
            imagePaymentItem.setImageResource(payment_items.iconId)
            itemView.setOnClickListener {
                listener.onClick(payment_items)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_page_transactions, parent, false)
        return PaymentMenuHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentMenuHolder, position: Int) {
        holder.bind(PaymentMenuTransactions.payment_menu_items_transactions[position], listener)
    }

    override fun getItemCount(): Int {
        return PaymentMenuTransactions.payment_menu_items_transactions.size
    }

    interface ListenerTransactions{
        fun onClick(itemMenuTransaction: PaymentMenuTransactions){}
    }

    data class PaymentMenuTransactions(val title: String, val iconId: Int){
        companion object {
            @JvmField
            val payment_menu_items_transactions = arrayOf(
                PaymentMenuTransactions("Между своими счетами", R.drawable.ic_between_your_accounts),
                PaymentMenuTransactions("По номеру карты",R.drawable.ic_transfer_to_card)
            )
        }
    }
}