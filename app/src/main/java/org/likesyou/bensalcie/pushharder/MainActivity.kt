package org.likesyou.bensalcie.pushharder

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var profileLabel: TextView
    private lateinit var usersLabel: TextView
    private lateinit var notificationLabel: TextView
    private var tvUsername: TextView? = null
    private lateinit var mViewPager: ViewPager
    private var pagerViewAdapter: PagerViewAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var myUsersDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        myUsersDatabase =
            FirebaseDatabase.getInstance().reference.child("FARM").child("Users")
        myUsersDatabase!!.keepSynced(true)
        profileLabel = findViewById(R.id.profileLabel)
        usersLabel = findViewById(R.id.usersLabel)
        notificationLabel = findViewById(R.id.notificationLabel)
        mViewPager = findViewById(R.id.mainViewPager)
        tvUsername = findViewById(R.id.useraname)
        profileLabel.setOnClickListener(View.OnClickListener { mViewPager.setCurrentItem(0) })
        usersLabel.setOnClickListener(View.OnClickListener { mViewPager.setCurrentItem(1) })
        notificationLabel.setOnClickListener(View.OnClickListener { mViewPager.setCurrentItem(2) })
        pagerViewAdapter = PagerViewAdapter(supportFragmentManager)
        mViewPager.setAdapter(pagerViewAdapter)
        mViewPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(i: Int) {
                changeTabs(i)
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    private fun changeTabs(position: Int) {
        if (position == 0) {
            profileLabel!!.setTextColor(resources.getColor(R.color.textTabBright))
            profileLabel!!.textSize = 22f
            usersLabel!!.setTextColor(resources.getColor(R.color.textTabLight))
            usersLabel!!.textSize = 16f
            notificationLabel!!.setTextColor(resources.getColor(R.color.textTabLight))
            notificationLabel!!.textSize = 16f
        }
        if (position == 1) {
            profileLabel!!.setTextColor(resources.getColor(R.color.textTabLight))
            profileLabel!!.textSize = 16f
            usersLabel!!.setTextColor(resources.getColor(R.color.textTabBright))
            usersLabel!!.textSize = 22f
            notificationLabel!!.setTextColor(resources.getColor(R.color.textTabLight))
            notificationLabel!!.textSize = 16f
        }
        if (position == 2) {
            profileLabel!!.setTextColor(resources.getColor(R.color.textTabLight))
            profileLabel!!.textSize = 16f
            usersLabel!!.setTextColor(resources.getColor(R.color.textTabLight))
            usersLabel!!.textSize = 16f
            notificationLabel!!.setTextColor(resources.getColor(R.color.textTabBright))
            notificationLabel!!.textSize = 22f
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        if (currentUser == null) {
            sendToLogin()
        } else {
            verifyUserDetails()
        }
    }

    private fun verifyUserDetails() {
        val userId = mAuth!!.currentUser!!.uid
        myUsersDatabase!!.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value.toString()
                    tvUsername!!.text =
                        "You are signed in as:  " + name.uppercase(Locale.getDefault()) + "  Sign out ? "
                    tvUsername!!.setOnClickListener {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("Confirm Action")
                        builder.setMessage("Do you want to Sign out?")
                        builder.setPositiveButton("Accept") { dialog, which ->
                            mAuth!!.signOut()
                            sendToLogin()
                        }
                        builder.setNegativeButton("Later", null)
                        builder.show()
                    }
                } else {
                    startActivity(Intent(this@MainActivity, UserDetailsActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendToLogin() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("NO") { dialog, id -> dialog.cancel() }
            .setNegativeButton("YES") { dialog, id -> finish() }
        val alert = builder.create()
        alert.show()
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED)
    }
}