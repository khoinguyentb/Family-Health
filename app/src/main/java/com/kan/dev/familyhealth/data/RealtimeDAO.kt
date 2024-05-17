package com.kan.dev.familyhealth.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kan.dev.familyhealth.utils.CHARACTERS
import com.kan.dev.familyhealth.utils.CODE_LENGTH
import java.util.Random


object RealtimeDAO {
    var databaseReference: DatabaseReference? = null
    fun initRealtimeData() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getOnetimeData(childPath: String?, listener: valueEventListener) {
        databaseReference!!.child(childPath!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        listener.onDataChange(snapshot)
                        Log.e("pushRealtimeData", "onCancelled:success")
                    } else {
                        Log.e("pushRealtimeData", "onCancelled:non-existent")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("pushRealtimeData", "onCancelled:" + error.message)
                }
            })
    }

    fun getRealtimeData(childPath: String?, listener: valueEventListener) {
        databaseReference!!.child(childPath!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listener.onDataChange(snapshot)
                    Log.e("pushRealtimeData", "onCancelled:success")
                } else {
                    Log.e("pushRealtimeData", "onCancelled:exists")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("pushRealtimeData", "onCancelled:" + error.message)
            }
        })
    }

    fun pushRealtimeData(childPath: String, newChild: Any, listener: onSuccessListener) {
        databaseReference!!.child(childPath).setValue(newChild)
            .addOnSuccessListener { unused: Void? ->
                Log.e("pushRealtimeData", "onCancelled:$childPath,$newChild")
                listener.onSuccess(unused)
            }.addOnFailureListener { v: Exception ->
                Log.e(
                    "aa",
                    v.message.toString()
                )
            }
    }

    fun updateRealtimeData(
        childPath: String,
        newChild: Map<String?, Any?>,
        listener: onSuccessListener
    ) {
        databaseReference!!.child(childPath).updateChildren(newChild)
            .addOnSuccessListener { unused: Void? ->
                Log.e("pushRealtimeData", "onCancelled:$childPath,$newChild")
                listener.onSuccess(unused)
            }
    }

    fun removeRealtimeData(childPath: String, listener: onSuccessListener) {
        databaseReference!!.child(childPath).removeValue().addOnSuccessListener { unused: Void? ->
            Log.e("removeRealtimeData", "OnSuccess:$childPath")
            listener.onSuccess(unused)
        }.addOnFailureListener { e: Exception? ->
            Log.e(
                "removeRealtimeData",
                "OnFailure:$childPath"
            )
        }
    }

    fun checkExistUser(value: String?, listener: valueEventListener) {
        databaseReference!!.child(value!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("pushRealtimeData", "onCancelled:success")
                    listener.onDataChange(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("checkExistUser", "onCancelled:" + error.message)
                }
            })
    }

    fun generateMyCode(): String {
        val random = Random()
        val codeBuilder: StringBuilder = StringBuilder(CODE_LENGTH)
        for (i in 0 until CODE_LENGTH) {
            val randomIndex = random.nextInt(CHARACTERS.length)
            val randomChar: Char = CHARACTERS[randomIndex]
            codeBuilder.append(randomChar)
        }
        return codeBuilder.toString()
    }


    fun interface valueEventListener {
        fun onDataChange(snapshot: DataSnapshot?)
    }

    fun interface onSuccessListener {
        fun onSuccess(unused: Void?)
    }
}

