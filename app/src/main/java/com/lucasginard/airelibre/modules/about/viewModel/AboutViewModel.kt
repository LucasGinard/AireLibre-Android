package com.lucasginard.airelibre.modules.about.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lucasginard.airelibre.modules.about.model.Contributor

class AboutViewModel() : ViewModel() {

    val listContributors = MutableLiveData<ArrayList<Contributor>>()

    fun getAllContributors(): MutableLiveData<ArrayList<Contributor>> {
        val rootRef = FirebaseDatabase.getInstance().reference
        val listAux = ArrayList<Contributor>()
        val orderDetailRef = rootRef.child("getContributors").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    postSnapshot.getValue(Contributor::class.java)?.let { listAux.add(it) }
                }
                listContributors.value = listAux
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        return listContributors
    }

}