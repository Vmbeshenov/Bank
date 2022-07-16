package com.vmbeshenov.bank.add_product

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
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.data.CardAccount
import com.vmbeshenov.bank.databinding.ActivityNewProductBinding
import com.vmbeshenov.bank.main.MainActivity
import kotlin.random.Random

class NewProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewProductBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val paymentsSystems = arrayOf("Mastercard", "Visa", "Мир")

    private var newCardName: String = paymentsSystems[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val user = auth.currentUser
        database = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference

        setSupportActionBar(binding.toolbarNewProduct)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Открыть новый счет"

        var id = Random.nextLong(7000000000000001, 7999999999999999)
        var isCompleteControl = false

        binding.buttonNewProductReady.setOnClickListener {
            val cardPinCode = Random.nextInt(1000, 9999)
            val cardCVC = Random.nextInt(100, 999)
            val newCardAccount = CardAccount(id.toString(), newCardName, 0.00, cardPIN = cardPinCode, cardCVC = cardCVC, user?.uid)
            val list: MutableList<CardAccount> = CardAccount.cardAccounts.toMutableList()
            list.add(newCardAccount)
            CardAccount.cardAccounts = list.toTypedArray()
            database.child("users").child(user!!.uid).child("cardAccounts").child(id.toString()).setValue(newCardAccount, id.toString())
            database.child("cardAccounts").child(id.toString()).setValue(newCardAccount, id.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        database.child("cardAccounts").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                while (!isCompleteControl) {
                    var isExist = false
                    for (snap in snapshot.children) {
                        val card = snap.getValue<CardAccount>()
                        val cardID = card!!.cardId.toString().toLong()
                        if (cardID == id) {
                            id++
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

        binding.buttonPaySystemMastercard.setOnClickListener {
            newCardName = paymentsSystems[0]
            binding.imageNewCard.setImageResource(R.drawable.payment_system_mastercard)
        }
        binding.buttonPaySystemVisa.setOnClickListener {
            newCardName = paymentsSystems[1]
            binding.imageNewCard.setImageResource(R.drawable.payment_system_visa)
        }
        binding.buttonPaySystemWorld.setOnClickListener {
            newCardName = paymentsSystems[2]
            binding.imageNewCard.setImageResource(R.drawable.payment_system_world)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val listCards: MutableList<CardAccount> = emptyList<CardAccount>().toMutableList()
        CardAccount.cardAccounts = listCards.toTypedArray()
    }
}