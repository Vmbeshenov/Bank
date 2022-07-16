package com.vmbeshenov.bank.payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.data.CardAccount
import com.vmbeshenov.bank.databinding.ActivityBetweenAccountsBinding
import kotlin.math.roundToInt

class BetweenAccountsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBetweenAccountsBinding
    private var database: DatabaseReference = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val auth: FirebaseAuth =  Firebase.auth
    private val user = auth.currentUser
    var itemsMap: MutableList<ItemsMap> = emptyList<ItemsMap>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBetweenAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.buttonBetweenAccContinue.setOnClickListener {
            if(validateForm()) {
                var cardIdWriteOff = ""
                var cardIdCredit = ""
                val cardDataWriteOf = binding.textFieldWriteOffAccount.text.toString()
                val cardDataCredit = binding.textFieldCreditAccount.text.toString()

                for (item in itemsMap) {
                    if (item.cardData == cardDataWriteOf) {
                        cardIdWriteOff = item.cardId
                    }
                    if (item.cardData == cardDataCredit) {
                        cardIdCredit = item.cardId
                    }
                }

                var sum = binding.editTransactionSumBetween.text.toString().toDouble()
                sum = (sum * 100).roundToInt() / 100.0
                val intent = Intent(this, BetweenAccountsConfirmationActivity::class.java)
                intent.putExtra("cardDataWriteOf", cardDataWriteOf)
                intent.putExtra("cardIdWriteOff", cardIdWriteOff)

                intent.putExtra("cardDataCredit", cardDataCredit)
                intent.putExtra("cardIdCredit", cardIdCredit)

                intent.putExtra("transactionSum", sum)
                startActivity(intent)
            }
        }

        val items: MutableList<String> = emptyList<String>().toMutableList()

        database.child("users").child(user!!.uid).child("cardAccounts").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children) {
                    val card = snap.getValue<CardAccount>()
                    var shortCardId = card!!.cardId!!
                    shortCardId = shortCardId.substring(shortCardId.length - 4)
                    val cardData = "${card.cardTitle} •••$shortCardId ${card.cardBalance}₽"
                    items.add(cardData)
                    itemsMap.add(ItemsMap(card.cardId!!, cardData, card.cardBalance!!))
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.textFieldWriteOffAccount.setAdapter(adapter)
        binding.textFieldCreditAccount.setAdapter(adapter)
    }

    private fun validateForm(): Boolean {
        var valid = true
        val cardDataWriteOf = binding.textFieldWriteOffAccount.text.toString()
        var cardSumWriteOff = 0.0

        if(binding.editTransactionSumBetween.text.toString() == ""){
            binding.editTransactionSumBetween.error = "Введите корректную сумму"
            valid = false
        }
        else {
            val sum = binding.editTransactionSumBetween.text.toString().toDouble()
            if(sum < 0.01){
                binding.editTransactionSumBetween.error = "Введите корректную сумму"
                valid = false
            }

            for (item in itemsMap) {
                if (item.cardData == cardDataWriteOf) {
                    cardSumWriteOff = item.cardBalance
                }
            }

            if (sum > cardSumWriteOff){
                binding.editTransactionSumBetween.error = "Недостаточно средств на выбранной карте"
                valid = false
            }
        }

        if(cardDataWriteOf == ""){
            binding.textMenuWriteOffAccount.error = "Обязательное поле"
            valid = false
        }
        else{
            binding.textMenuWriteOffAccount.error = null
        }

        val cardDataCredit = binding.textFieldCreditAccount.text.toString()
        if(cardDataCredit == ""){
            binding.textMenuCreditAccount.error = "Обязательное поле"
            valid = false
        }
        else{
            binding.textMenuCreditAccount.error = null
        }

        if (cardDataWriteOf == cardDataCredit && cardDataWriteOf != ""){
            binding.textMenuWriteOffAccount.error = "Вы указали одинаковые счета"
            binding.textMenuCreditAccount.error = "Вы указали одинаковые счета"
            valid = false
        }
        return valid
    }

    data class ItemsMap(var cardId: String, var cardData: String, var cardBalance: Double)
}