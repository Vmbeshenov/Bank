package com.vmbeshenov.bank.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.data.CardAccount
import com.vmbeshenov.bank.databinding.ItemMainCardAccountsBinding

class CardAccountsAdapter(private val listener: Listener): RecyclerView.Adapter<CardAccountsAdapter.CardAccountsHolder>(){

    class CardAccountsHolder(item: View): RecyclerView.ViewHolder(item){

        private val binding = ItemMainCardAccountsBinding.bind(item)

        fun bind(cardAcc: CardAccount, listener: Listener) = with(binding){
            cardAccountTitle.text = cardAcc.cardTitle
            cardAccountSum.text = cardAcc.cardBalance.toString()
            var shortCardId = cardAcc.cardId.toString()
            shortCardId = shortCardId.substring(shortCardId.length - 4)
            cardAccountId.text = shortCardId
            itemView.setOnClickListener {
                listener.onClick(cardAcc)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAccountsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_card_accounts, parent, false)
        return CardAccountsHolder(view)
    }

    override fun onBindViewHolder(holder: CardAccountsHolder, position: Int) {
        holder.bind(CardAccount.cardAccounts[position], listener)
    }

    override fun getItemCount(): Int {
        return CardAccount.cardAccounts.size
    }

    interface Listener{
        fun onClick(cardAcc: CardAccount){}
    }
}