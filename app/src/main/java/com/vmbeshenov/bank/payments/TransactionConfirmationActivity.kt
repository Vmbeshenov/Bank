package com.vmbeshenov.bank.payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.vmbeshenov.bank.databinding.ActivityTransactionConfirmationBinding
import com.vmbeshenov.bank.main.MainActivity
import java.time.LocalDate
import kotlin.math.roundToInt
import kotlin.random.Random

class TransactionConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionConfirmationBinding
    private var database: DatabaseReference = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val auth: FirebaseAuth =  Firebase.auth
    private val user = auth.currentUser
    private var personIdDestination = ""
    private var isComplete = false
    private var cardSumWriteOf = -1.0
    private var cardSumDestination = 0.0
    private var transactionSum = 0.0
    private var cardIdWriteOff = ""
    private var listCards: MutableList<CardAccount> = CardAccount.cardAccounts.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sum = intent.getSerializableExtra("transactionSum")
        transactionSum = sum.toString().toDouble()
        binding.textSumTransactionValue.text = transactionSum.toString()

        val cardDataWriteOf = intent.getSerializableExtra("cardDataWriteOf").toString()
        binding.textWriteOfTransactionValue.text = cardDataWriteOf

        cardIdWriteOff = intent.getSerializableExtra("cardIdWriteOff").toString()

        val cardIdDestination = intent.getSerializableExtra("cardIdDestination").toString()
        val shortCardIdDestination = cardIdDestination.substring(cardIdDestination.length - 4)
        binding.textDestinationCardShortNumber.text = shortCardIdDestination


        var idOperation = Random.nextInt(1000000, 9999999)
        var isCompleteControl = false

        setSupportActionBar(binding.toolbar3)
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


        database.child("cardAccounts").child(cardIdDestination).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val card = snapshot.getValue<CardAccount>()
                personIdDestination = card!!.personId!!
                cardSumDestination = card.cardBalance!!

                database.child("users").child(personIdDestination).child("phone").get().addOnSuccessListener {
                    val phoneNumber = it.value
                    if (phoneNumber != null) {
                        binding.textDestinationPhone.text = phoneNumber.toString()
                        binding.textDestinationPhone.visibility = View.VISIBLE
                    }
                }

                database.child("users").child(personIdDestination).child("email").get().addOnSuccessListener {
                    val email = it.value
                    if (email != null) {
                        binding.textDestinationEmail.text = email.toString()
                        binding.textDestinationEmail.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.buttonTransactionConfirm.setOnClickListener {
        isComplete = false
        calculateTransaction()
            if(isComplete) {
                database.child("users").child(user.uid).child("cardAccounts")
                    .child(cardIdWriteOff).child("cardBalance")
                    .setValue(cardSumWriteOf, "cardBalance")
                database.child("cardAccounts").child(cardIdWriteOff).child("cardBalance")
                    .setValue(cardSumWriteOf, "cardBalance")
                database.child("users").child(personIdDestination).child("cardAccounts")
                    .child(cardIdDestination).child("cardBalance")
                    .setValue(cardSumDestination, "cardBalance")
                database.child("cardAccounts").child(cardIdDestination).child("cardBalance")
                    .setValue(cardSumDestination, "cardBalance")

                var dateOperation = OperationsHistory(
                    idOperation.toString(),
                    "Платежи и переводы",
                    "Перевод на карту",
                    -transactionSum,
                    LocalDate.now().toString(),
                    cardIdWriteOff
                )

                database.child("users").child(user.uid).child("operationHistory")
                    .child(idOperation.toString()).setValue(dateOperation, idOperation.toString())

                dateOperation = OperationsHistory(
                    idOperation.toString(),
                    "Платежи и переводы",
                    "Денежный перевод",
                    transactionSum,
                    LocalDate.now().toString(),
                    cardIdDestination
                )

                database.child("users").child(personIdDestination).child("operationHistory")
                    .child(idOperation.toString()).setValue(dateOperation, idOperation.toString())


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else return@setOnClickListener
        }
    }

    private fun calculateTransaction(){
        var isCardFound = false
        for (cardItem in CardAccount.cardAccounts){
            if (cardItem.cardId == cardIdWriteOff){
                cardSumWriteOf = cardItem.cardBalance!!
                isCardFound = true
            }
        }
        if (isCardFound) {
            cardSumWriteOf -= transactionSum
            cardSumWriteOf = (cardSumWriteOf * 100).roundToInt() / 100.0

            cardSumDestination += transactionSum
            cardSumDestination = (cardSumDestination * 100).roundToInt() / 100.0

            isComplete = true
        }
        else{
            Toast.makeText(baseContext, "Ошибка: карта не найдена", Toast.LENGTH_SHORT).show()
        }
    }
}