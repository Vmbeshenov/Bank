package com.vmbeshenov.bank.bank_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vmbeshenov.bank.adapters.OperationsHistoryAdapter
import com.vmbeshenov.bank.data.Constants
import com.vmbeshenov.bank.data.OperationsHistory
import com.vmbeshenov.bank.databinding.FragmentBankAccountHistoryBinding

class BankAccountHistoryFragment : Fragment() {

    private lateinit var binding: FragmentBankAccountHistoryBinding
    private val adapter = OperationsHistoryAdapter()
    private var database: DatabaseReference = Firebase.database("https://bank-58595-default-rtdb.europe-west1.firebasedatabase.app/").reference


    private val auth: FirebaseAuth =  Firebase.auth
    private val user = auth.currentUser

    val listOperations: MutableList<OperationsHistory> = OperationsHistory.operations.toMutableList()
    private val dataModel: DataModel by activityViewModels()
    var selectedCardID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBankAccountHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataModel.cardId.observe(activity as LifecycleOwner) {
            selectedCardID = it
        }

        binding.apply {
            rcViewCardHistory.layoutManager = LinearLayoutManager(activity)
            rcViewCardHistory.adapter = adapter
        }

        database.child("users").child(user!!.uid).child("operationHistory").orderByChild("date").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children) {
                    val historyItem = snap.getValue<OperationsHistory>()
                    var isContentOperation = false
                    for(item in OperationsHistory.operations){
                        if(item.operationId == historyItem!!.operationId){
                            isContentOperation = true
                        }
                    }
                    if(!isContentOperation && historyItem!!.cardId == selectedCardID){
                        listOperations.add(historyItem)
                    }
                }
                if (!Constants.constant.isReversed) {
                    listOperations.reverse()
                    Constants.constant.isReversed = true
                }
                OperationsHistory.operations = listOperations.toTypedArray()
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listOperations.clear()
        OperationsHistory.operations = listOperations.toTypedArray()
        Constants.constant.isReversed = false
    }
}