package com.vmbeshenov.bank.bank_account

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.vmbeshenov.bank.R
import com.vmbeshenov.bank.databinding.ActivityBankAccountBinding
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.viewModels

class BankAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBankAccountBinding
    private val paymentsSystems = arrayOf("Mastercard", "Visa", "Мир")
    private var visibleInfo = false
    private var visiblePin = false
    private var visibleCVC = false
    private val dataModel: DataModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cardID = intent.getSerializableExtra("cardId").toString()
        val cardTitle = intent.getSerializableExtra("cardTitle").toString()
        val cardBalance = intent.getSerializableExtra("cardBalance").toString()
        val cardPIN = intent.getSerializableExtra("cardPIN").toString()
        val cardCVC = intent.getSerializableExtra("cardCVC").toString()

        setSupportActionBar(binding.toolbarCardAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        dataModel.cardId.value = cardID

        val shortCardId = cardID.substring(cardID.length - 4)
        val longCardIdToShow = cardID.chunked(4).joinToString(" ")

        binding.textCardIdShort.text = shortCardId
        binding.textCardTitle.text = cardTitle
        binding.textCardSum.text = cardBalance
        binding.textCardIdLong.text = longCardIdToShow

        when(cardTitle){
            paymentsSystems[0] -> {
                binding.imageCardLogo.setImageResource(R.drawable.payment_system_mastercard)
            }
            paymentsSystems[1] -> {
                binding.imageCardLogo.setImageResource(R.drawable.payment_system_visa)
            }
            paymentsSystems[2] -> {
                binding.imageCardLogo.setImageResource(R.drawable.payment_system_world)
            }
        }

        binding.textCardIdLong.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText(binding.textCardIdLong.text, cardID)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(baseContext, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
        }

        binding.textCardPinValue.setOnClickListener {
            if(visiblePin) {
                binding.textCardPinValue.text = "••••"
                binding.textCardPinValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                visiblePin = false
            }
            else {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(
                    binding.textCardPinValue.text,
                    cardPIN
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(baseContext, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
                binding.textCardPinValue.text = cardPIN
                binding.textCardPinValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_add_info,0,0,0)
                visiblePin = true
            }
        }

        binding.textCardCvcValue.setOnClickListener {
            if(visibleCVC){
                binding.textCardCvcValue.text = "•••"
                binding.textCardCvcValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                visibleCVC = false
            }
            else{
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(binding.textCardCvcValue.text, cardCVC)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(baseContext, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
                binding.textCardCvcValue.text = cardCVC
                binding.textCardCvcValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_add_info,0,0,0)
                visibleCVC = true
            }
        }

        binding.imageCardInfo.setOnClickListener {
            binding.apply {
                if (!visibleInfo){
                    textCardSymbol.visibility = View.GONE
                    textCardSum.visibility = View.GONE
                    textCardIdLong.visibility = View.VISIBLE
                    textCardCvcTitle.visibility = View.VISIBLE
                    textCardCvcValue.visibility = View.VISIBLE
                    textCardPinTitle.visibility = View.VISIBLE
                    textCardPinValue.visibility = View.VISIBLE
                    imageCardInfo.setImageResource(R.drawable.ic_info_close)
                    visibleInfo = true
                }
                else{
                    binding.textCardCvcValue.text = "•••"
                    binding.textCardCvcValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    visibleCVC = false

                    binding.textCardPinValue.text = "••••"
                    binding.textCardPinValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    visiblePin = false

                    textCardSymbol.visibility = View.VISIBLE
                    textCardSum.visibility = View.VISIBLE
                    textCardIdLong.visibility = View.GONE
                    textCardCvcTitle.visibility = View.GONE
                    textCardCvcValue.visibility = View.GONE
                    textCardPinTitle.visibility = View.GONE
                    textCardPinValue.visibility = View.GONE
                    imageCardInfo.setImageResource(R.drawable.ic_info)
                    visibleInfo = false
                }
            }
        }

        binding.tabCardAccount.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        supportFragmentManager.beginTransaction().replace(binding.fragContainerCardAcc.id, BankAccountActionsFragment()).commit()
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction().replace(binding.fragContainerCardAcc.id, BankAccountHistoryFragment()).commit()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}