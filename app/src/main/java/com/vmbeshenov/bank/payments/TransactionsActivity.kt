package com.vmbeshenov.bank.payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.vmbeshenov.bank.databinding.ActivityTransactionsBinding
import kotlin.math.roundToInt

class TransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsBinding
    private var database: DatabaseReference = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val auth: FirebaseAuth =  Firebase.auth
    private val user = auth.currentUser
    private var itemsMap: MutableList<BetweenAccountsActivity.ItemsMap> = emptyList<BetweenAccountsActivity.ItemsMap>().toMutableList()
    private var isCorrectCardNumber = false
    private var cardIdDestination = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.editTransactionCardNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.editTransactionCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_main_card, 0, 0,0)
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                cardIdDestination = binding.editTransactionCardNumber.text.toString()
                isCorrectCardNumber = false
                /*
                if (isCorrectCardNumber){
                binding.editTransactionCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_main_card, 0, R.drawable.ic_done,0)
                }
                 */

                database.child("cardAccounts").get().addOnSuccessListener {
                    for (snap in it.children) {
                        val card = snap.getValue<CardAccount>()
                        val cardID = card!!.cardId.toString()
                        if (cardID == cardIdDestination) {
                            isCorrectCardNumber = true
                            binding.editTransactionCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_main_card, 0, R.drawable.ic_done,0)
                            break
                        }
                    }
                }
            }
        })

        binding.buttonTransactionContinue.setOnClickListener {
            if (validateForm()) {
                cardIdDestination = binding.editTransactionCardNumber.text.toString()
                if (isCorrectCardNumber) {
                    var cardIdWriteOff = ""
                    val cardDataWriteOf = binding.textFieldTransactionYourWriteOffAccount.text.toString()

                    for (item in itemsMap) {
                        if (item.cardData == cardDataWriteOf) {
                            cardIdWriteOff = item.cardId
                        }
                    }
                    var sum = binding.editTransactionSumToCard.text.toString().toDouble()
                    sum = (sum * 100).roundToInt() / 100.0

                    val intent = Intent(this, TransactionConfirmationActivity::class.java)
                    intent.putExtra("cardDataWriteOf", cardDataWriteOf)
                    intent.putExtra("cardIdWriteOff", cardIdWriteOff)

                    intent.putExtra("cardIdDestination", cardIdDestination)

                    intent.putExtra("transactionSum", sum)
                    startActivity(intent)
                } else {
                    binding.editTransactionCardNumber.error = "Карта не найдена"
                }
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
                    itemsMap.add(BetweenAccountsActivity.ItemsMap(card.cardId!!, cardData, card.cardBalance!!))
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.textFieldTransactionYourWriteOffAccount.setAdapter(adapter)
    }

    private fun validateForm(): Boolean {
        var valid = true
        val cardDataWriteOf = binding.textFieldTransactionYourWriteOffAccount.text.toString()
        var cardSumWriteOff = 0.0

        if(binding.editTransactionSumToCard.text.toString() == ""){
            binding.editTransactionSumToCard.error = "Введите корректную сумму"
            valid = false
        }
        else {
            val sum = binding.editTransactionSumToCard.text.toString().toDouble()
            if(sum < 0.01){
                binding.editTransactionSumToCard.error = "Введите корректную сумму"
                valid = false
            }
            for (item in itemsMap) {
                if (item.cardData == cardDataWriteOf) {
                    cardSumWriteOff = item.cardBalance
                }
            }

            if (sum > cardSumWriteOff){
                binding.editTransactionSumToCard.error = "Недостаточно средств на выбранной карте"
                valid = false
            }
        }

        if(binding.editTransactionCardNumber.text.toString().length < 16){
            binding.editTransactionCardNumber.error = "Введите номер карты"
            valid = false
        }

        if(cardDataWriteOf == ""){
            binding.transactionYourWriteOffAccount.error = "Обязательное поле"
            valid = false
        }
        else{
            binding.transactionYourWriteOffAccount.error = null
        }
        return valid
    }
}