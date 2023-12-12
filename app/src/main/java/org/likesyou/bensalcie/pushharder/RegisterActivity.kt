package org.likesyou.bensalcie.pushharder

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity constructor() : AppCompatActivity() {
    private lateinit var etAdmNo: EditText
    private lateinit var etPass: EditText
    private lateinit var etPassOne: EditText
    private lateinit var btnRegister: Button
    private var mAuth: FirebaseAuth? = null
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etAdmNo = findViewById(R.id.etAdm)
        etPass = findViewById(R.id.etPassword)
        etPassOne = findViewById(R.id.etPassword)
        pd = ProgressDialog(this)
        pd!!.setTitle("Creating Account")
        pd!!.setMessage("Just a moment...")
        mAuth = FirebaseAuth.getInstance()
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                pd!!.show()
                val adm: String = etAdmNo.getText().toString().trim({ it <= ' ' })
                val pass: String = etPass.getText().toString().trim({ it <= ' ' })
                val pass1: String = etPassOne.getText().toString().trim({ it <= ' ' })
                if (!TextUtils.isEmpty(adm) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(pass1)) {
                    if ((pass == pass1)) {
                        //toast("Data ready for upload....");
                        mAuth!!.createUserWithEmailAndPassword(adm, pass)
                            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                                public override fun onComplete(task: Task<AuthResult?>) {
                                    if (task.isSuccessful()) {
                                        toast("Account created Successfully, Login to verify")
                                        pd!!.dismiss()
                                        mAuth!!.signOut()
                                        startActivity(
                                            Intent(
                                                this@RegisterActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        pd!!.dismiss()
                                        toast(task.getException()!!.message)
                                    }
                                }
                            })
                    } else {
                        pd!!.dismiss()
                        toast("Passwords dont match....")
                    }
                } else {
                    pd!!.dismiss()
                    toast("You left a blank !")
                }
            }
        })
    }

    fun signin(view: View?) {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
    }

    private fun toast(s: String?) {
        Toast.makeText(this, "Message: " + s, Toast.LENGTH_SHORT).show()
    }
}