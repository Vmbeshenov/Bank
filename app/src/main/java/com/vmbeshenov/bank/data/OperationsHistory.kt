package com.vmbeshenov.bank.data
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class OperationsHistory(var operationId: String? = null, var category: String? = null, var destination: String? = null, var credit: Double? = null,
                             var date: String? = null, var cardId: String? = null)
{
    companion object {
        @JvmField
        var operations = arrayOf<OperationsHistory>()
    }
}