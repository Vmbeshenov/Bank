package com.vmbeshenov.bank.authorization

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.main.MainActivity
import com.vmbeshenov.bank.databinding.FragmentSingUpBinding

class SingUpFragment : Fragment() {

    private lateinit var binding: FragmentSingUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSingUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference

        binding.buttonSingUp.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            createAccount(email, password)
        }
    }

    private fun createAccount(email: String, password: String) {
        if (!validateForm()) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    database.child("users").child(user!!.uid).child("email").setValue(user.email, "email")
                    updateUI(user)
                } else {
                    Toast.makeText(context, "Ошибка регистрации", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.editEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.editEmail.error = "Обязательное поле"
            valid = false
        } else {
            binding.editEmail.error = null
        }

        val password = binding.editPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.editPassword.error = "Обязательное поле"
            valid = false
        } else {
            binding.editPassword.error = null
        }

        val passwordRepeat = binding.editPasswordRepeat.text.toString()
        if(TextUtils.isEmpty(passwordRepeat)){
            binding.editPasswordRepeat.error = "Обязательное поле"
            valid = false
        } else if (password != passwordRepeat){
            binding.editPasswordRepeat.error = "Пароли не совпадают"
            valid = false
        } else {
            binding.editPasswordRepeat.error = null
        }

        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }
    }
}