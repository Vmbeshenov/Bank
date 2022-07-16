package com.vmbeshenov.bank.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.main.MainActivity
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.databinding.ActivitySingInPhoneBinding
import java.util.concurrent.TimeUnit

class SingInPhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingInPhoneBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference

        binding.buttonGetPhoneCode.setOnClickListener {
            if (!validatePhoneNumber()) {
                return@setOnClickListener
            }
            val phoneNumber = binding.editPhoneNumber.text.toString()
            startPhoneNumberVerification(phoneNumber)
            changePhoneAuthUI()
        }

        binding.buttonSingInWithPhone.setOnClickListener {
            val phoneCode = binding.editPhoneCode.text.toString()
            if (TextUtils.isEmpty(phoneCode)) {
                binding.editPhoneCode.error = "Обязательное поле"
                return@setOnClickListener
            }
            verifyPhoneNumberWithCode(storedVerificationId, phoneCode)
        }

        binding.buttonToEmailAuth.setOnClickListener {
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verificationInProgress = false
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                verificationInProgress = false
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(baseContext, "Invalid phone number.",
                        Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(baseContext, "Quota exceeded.",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun changePhoneAuthUI(){
        binding.editPhoneNumber.isEnabled = false
        binding.editPhoneCode.visibility = View.VISIBLE
        binding.buttonSingInWithPhone.visibility = View.VISIBLE
        binding.buttonGetPhoneCode.visibility = View.GONE
        binding.phoneAuthTitleDescription.setText(R.string.phone_auth_title_continuation)
        binding.phoneAuthDescription.setText(R.string.phone_auth_description_continuation)
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = binding.editPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.editPhoneNumber.error = "Обязательное поле"
            return false
        }
        return true
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        verificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    database.child("users").child(user!!.uid).child("phone").setValue(user.phoneNumber, "phone")
                    updateUI(user)
                } else {
                    Toast.makeText(this, "Ошибка входа", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}