package com.vmbeshenov.bank.bank_account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel : ViewModel() {
    val cardId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}