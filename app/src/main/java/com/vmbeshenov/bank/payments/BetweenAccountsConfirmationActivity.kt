package com.vmbeshenov.bank.payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.data.CardAccount
import com.vmbeshenov.bank.data.OperationsHistory
import com.vmbeshenov.bank.databinding.ActivityBetweenAccountsConfirmationBinding
import com.vmbeshenov.bank.main.MainActivity
import java.time.LocalDate
import kotlin.math.roundToInt
import kotlin.random.Random

class BetweenAccountsConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBetweenAccountsConfirmationBinding
    private var database: DatabaseReference = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val auth: FirebaseAuth =  Firebase.auth
    private val user = auth.currentUser
    private var listCards: MutableList<CardAccount> = CardAccount.cardAccounts.toMutableList()
    private var cardSumWriteOf = 0.0
    private var cardSumCredit = 0.0
    private var cardIdWriteOff = ""
    private var cardIdCredit = ""
    private var isComplete = false
    private var transactionSum = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBetweenAccountsConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sum = intent.getSerializableExtra("transactionSum")
        transactionSum = sum.toString().toDouble()
        binding.textSumBetweenAccValue.text = transactionSum.toString()

        val cardDataWriteOf = intent.getSerializableExtra("cardDataWriteOf").toString()
        binding.textWriteOfBetweenAccValue.text = cardDataWriteOf
        val cardDataCredit = intent.getSerializableExtra("cardDataCredit").toString()
        binding.textCreditBetweenAccValue.text = cardDataCredit

        cardIdWriteOff = intent.getSerializableExtra("cardIdWriteOff").toString()
        cardIdCredit = intent.getSerializableExtra("cardIdCredit").toString()

        var idOperation = Random.nextInt(1000000, 9999999)
        var isCompleteControl = false

        setSupportActionBar(binding.toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        database.child("users").child(user!!.uid).child("cardAccounts").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children) {

                    val card = snap.getValue<CardAccount>()
                    var isContentCard = false
                    for(item in CardAccount.cardAccounts){
                        if(item.cardId == card!!.cardId){
                            isContentCard = true
                        }
                    }
                    if(!isContentCard){
                        listCards.add(card!!)
                    }
                    CardAccount.cardAccounts = listCards.toTypedArray()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        database.child("users").child(user.uid).child("operationHistory").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                while (!isCompleteControl) {
                    var isExist = false
                    for (snap in snapshot.children) {
                        val operation = snap.getValue<OperationsHistory>()
                        val operationID = operation!!.operationId.toString().toInt()
                        if (operationID == idOperation) {
                            idOperation++
                            isExist = true
                        }
                    }
                    if(!isExist){
                        isCompleteControl = true
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.buttonBetweenAccConfirm.setOnClickListener {
            isComplete = false
            calculateTransaction()
            if(isComplete) {
                database.child("users").child(user.uid).child("cardAccounts").child(cardIdWriteOff)
                    .child("cardBalance").setValue(cardSumWriteOf, "cardBalance")
                database.child("cardAccounts").child(cardIdWriteOff).child("cardBalance")
                    .setValue(cardSumWriteOf, "cardBalance")

                database.child("users").child(user.uid).child("cardAccounts").child(cardIdCredit)
                    .child("cardBalance").setValue(cardSumCredit, "cardBalance")
                database.child("cardAccounts").child(cardIdCredit).child("cardBalance")
                    .setValue(cardSumCredit, "cardBalance")


                val dateOperation = OperationsHistory(
                    idOperation.toString(),
                    "Платежи и переводы",
                    "Между своими счетами",
                    transactionSum,
                    LocalDate.now().toString(),
                    cardIdCredit
                )

                database.child("users").child(user.uid).child("operationHistory")
                    .child(idOperation.toString()).setValue(dateOperation, idOperation.toString())

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else return@setOnClickListener
        }
    }
    private fun calculateTransaction(){
        for (cardItem in CardAccount.cardAccounts){
            if (cardItem.cardId == cardIdCredit){
                cardSumCredit = cardItem.cardBalance!!
            }
            if (cardItem.cardId == cardIdWriteOff){
                cardSumWriteOf = cardItem.cardBalance!!
            }
        }
        cardSumWriteOf -= transactionSum
        cardSumWriteOf = (cardSumWriteOf * 100).roundToInt() / 100.0

        cardSumCredit += transactionSum
        cardSumCredit = (cardSumCredit * 100).roundToInt() / 100.0
        isComplete = true
    }
}