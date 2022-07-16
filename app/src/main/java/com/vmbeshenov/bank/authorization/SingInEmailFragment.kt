package com.vmbeshenov.bank.authorization

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.main.MainActivity
import com.vmbeshenov.bank.databinding.FragmentSingInEmailBinding

class SingInEmailFragment : Fragment() {

    private lateinit var binding: FragmentSingInEmailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSingInEmailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.buttonSingInWithEmail.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            signIn(email, password)
        }
        binding.buttonToPhoneAuth.setOnClickListener {
            val intent = Intent(this.context, SingInPhoneActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(context, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
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
        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }
    }
}