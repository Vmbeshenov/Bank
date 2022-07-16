package com.vmbeshenov.bank.authorization

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.main.MainActivity
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.databinding.ActivityAuthorizationBinding

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAuthorizationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.authBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.sing_in_item -> {
                    supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, SingInEmailFragment()).commit()
                    true
                }
                R.id.sing_up_item -> {
                    supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, SingUpFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}