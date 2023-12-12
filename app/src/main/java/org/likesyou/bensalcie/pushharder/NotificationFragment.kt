package org.likesyou.bensalcie.pushharder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment constructor() : Fragment() {
    private lateinit var v: View
    private var tvNotif: TextView? = null
    private var mAuth: FirebaseAuth? = null
    private var myUsersDatabase: DatabaseReference? = null
    private var userId: String? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notification, container, false)
        mAuth = FirebaseAuth.getInstance()
        myUsersDatabase =
            FirebaseDatabase.getInstance().getReference().child("FARM").child("Users")
        userId = mAuth!!.getCurrentUser()!!.getUid()
        tvNotif = v.findViewById(R.id.firstNotification)
        setupUploader()
        return v
    }

    private fun setupUploader() {
        myUsersDatabase!!.child((userId)!!).addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name: String = dataSnapshot.child("name").getValue().toString()
                    tvNotif!!.setText("Hello,  " + name + ". Thank you for being part of Farmers Online Selling, we look forward to make this place a better platform to sell your products as you maximize your profits . We guarantee you a free platform to sell all your products and new customers as well. Stay tuned to have more of our services in future.")
                }
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}