package com.vmbeshenov.bank.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.adapters.CardAccountsAdapter
import com.vmbeshenov.bank.add_product.NewProductActivity
import com.vmbeshenov.bank.bank_account.BankAccountActivity
import com.vmbeshenov.bank.data.CardAccount
import com.vmbeshenov.bank.databinding.FragmentMainPageBinding
import kotlin.math.roundToInt

class MainPageFragment : Fragment(), CardAccountsAdapter.Listener {

    private lateinit var binding: FragmentMainPageBinding
    private var database: DatabaseReference = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val adapter = CardAccountsAdapter(this)
    var listCards: MutableList<CardAccount> = emptyList<CardAccount>().toMutableList()

    private val auth: FirebaseAuth =  Firebase.auth
    private val user = auth.currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CardAccount.cardAccounts = listCards.toTypedArray()
        listCards = CardAccount.cardAccounts.toMutableList()

        var totalSum = 0.0
        val email = user?.email
        val phoneNumber = user?.phoneNumber

        if (email != "" && email != null) {
            binding.textMainPageEmail.text = email
            binding.textMainPageEmail.visibility = View.VISIBLE
        } else {
            binding.textMainPagePhone.text = phoneNumber
            binding.textMainPagePhone.visibility = View.VISIBLE
        }

        binding.apply {
            recyclerMainCardAccounts.layoutManager = LinearLayoutManager(activity)
            recyclerMainCardAccounts.adapter = adapter
        }

        binding.buttonAddNewProduct.setOnClickListener {
            val intent = Intent(this.context, NewProductActivity::class.java)
            startActivity(intent)
        }

        database.child("users").child(user!!.uid).child("cardAccounts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children) {
                    val cardAccountUser = snap.getValue<CardAccount>()
                    totalSum += cardAccountUser!!.cardBalance!!.toDouble()
                    var isContentCard = false
                    for(item in CardAccount.cardAccounts){
                        if(item.cardId == cardAccountUser.cardId){
                            isContentCard = true
                        }
                    }
                    if(!isContentCard){
                        listCards.add(cardAccountUser)
                    }
                }
                CardAccount.cardAccounts = listCards.toTypedArray()
                totalSum = (totalSum * 100).roundToInt() / 100.0
                binding.textMainBalanceSum.text = totalSum.toString()
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    override fun onClick(cardAcc: CardAccount) {
        val intent = Intent(this.context, BankAccountActivity::class.java)
        intent.putExtra("cardId", cardAcc.cardId)
        intent.putExtra("cardTitle", cardAcc.cardTitle)
        intent.putExtra("cardBalance", cardAcc.cardBalance)
        intent.putExtra("cardPIN", cardAcc.cardPIN)
        intent.putExtra("cardCVC", cardAcc.cardCVC)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listCards = emptyList<CardAccount>().toMutableList()
        CardAccount.cardAccounts = listCards.toTypedArray()
    }
}