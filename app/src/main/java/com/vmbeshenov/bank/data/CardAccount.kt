package com.vmbeshenov.bank.data
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CardAccount(var cardId: String? = null, var cardTitle: String? = null,
                       var cardBalance : Double? = null, var cardPIN: Int? = null, var cardCVC: Int? = null, var personId: String? = null){
    companion object {
        @JvmField
        var cardAccounts = arrayOf<CardAccount>()
    }
}