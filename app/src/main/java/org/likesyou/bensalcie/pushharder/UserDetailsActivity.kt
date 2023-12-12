package org.likesyou.bensalcie.pushharder

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserDetailsActivity constructor() : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etCounty: EditText
    private lateinit var etSubcounty: EditText
    private lateinit var rbCropGrowing: RadioButton
    private lateinit var rbAnimalRearing: RadioButton
    private lateinit var rbSmallScale: RadioButton
    private lateinit var rbLargeScale: RadioButton
    private lateinit var btnUpdate: Button
    private var type_farming: String = ""
    private var type_scale: String = ""
    private var myUsersDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etCounty = findViewById(R.id.etCounty)
        etSubcounty = findViewById(R.id.etSubCounty)
        pd = ProgressDialog(this)
        pd!!.setTitle("Completing profile setup")
        pd!!.setMessage("Just a moment")
        rbCropGrowing = findViewById(R.id.cropGrowing)
        rbAnimalRearing = findViewById(R.id.animalRearing)
        rbSmallScale = findViewById(R.id.smallScale)
        rbLargeScale = findViewById(R.id.largeScale)
        btnUpdate = findViewById(R.id.btnUpdate)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth!!.getCurrentUser()!!.getUid()
        myUsersDatabase =
            FirebaseDatabase.getInstance().getReference().child("FARM").child("Users")
        btnUpdate.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                pd!!.show()
                if (rbCropGrowing.isChecked()) {
                    type_farming = "Crop Growing"
                }
                if (rbAnimalRearing.isChecked()) {
                    type_farming = "Animal Rearing"
                }
                if (rbSmallScale.isChecked()) {
                    type_scale = "Small Scale"
                }
                if (rbLargeScale.isChecked()) {
                    type_scale = "Large Scale"
                }
                val name: String = etName.getText().toString().trim({ it <= ' ' })
                val phone: String = etPhone.getText().toString().trim({ it <= ' ' })
                val county: String = etCounty.getText().toString().trim({ it <= ' ' })
                val subCounty: String = etSubcounty.getText().toString().trim({ it <= ' ' })
                if ((!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)
                            && !TextUtils.isEmpty(county) && !TextUtils.isEmpty(name)
                            && !TextUtils.isEmpty(subCounty)
                            && !TextUtils.isEmpty(type_scale) && !TextUtils.isEmpty(type_farming))
                ) {
                    //toast(name+type_farming+type_scale);
                    uploadData(name, phone, county, subCounty, type_scale, type_farming)
                } else {
                    pd!!.dismiss()
                    toast("You left some blanks")
                }
            }
        })
    }

    private fun uploadData(
        name: String,
        phone: String,
        county: String,
        subCounty: String,
        type_scale: String,
        type_farming: String
    ) {
        val newUser: DatabaseReference = myUsersDatabase!!.child((userId)!!)
        val myMap: HashMap<String, Any> = HashMap()
        myMap.put("name", name)
        myMap.put("phone", phone)
        myMap.put("county", county)
        myMap.put("subcounty", subCounty)
        myMap.put("type_scale", type_scale)
        myMap.put("type_farming", type_farming)
        newUser.updateChildren(myMap).addOnCompleteListener(object : OnCompleteListener<Void?> {
            public override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful()) {
                    pd!!.dismiss()
                    startActivity(Intent(this@UserDetailsActivity, MainActivity::class.java))
                    finish()
                } else {
                    pd!!.dismiss()
                    toast(task.getException()!!.message)
                }
            }
        })
    }

    private fun toast(s: String?) {
        Toast.makeText(this, "Message: " + s, Toast.LENGTH_SHORT).show()
    }
}