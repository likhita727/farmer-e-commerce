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
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var etAdmNo: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private var mFirebaseAuth: FirebaseAuth? = null
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etAdmNo = findViewById(R.id.etAdm)
        etPass = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        mFirebaseAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(this)
        pd!!.setTitle("Logging in")
        pd!!.setMessage("Just a moment...")
        btnLogin.setOnClickListener(View.OnClickListener {
            pd!!.show()
            val adm = etAdmNo.getText().toString().trim { it <= ' ' }
            val pass = etPass.getText().toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(adm) && !TextUtils.isEmpty(pass)) {
                mFirebaseAuth!!.signInWithEmailAndPassword(adm, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            pd!!.dismiss()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            pd!!.dismiss()
                            toast(task.exception!!.message)
                        }
                    }
            } else {
                pd!!.dismiss()
                toast("You left a blank !")
            }
        })
    }

    private fun toast(s: String?) {
        Toast.makeText(this, "Message: $s", Toast.LENGTH_SHORT).show()
    }

    fun signup(view: View?) {
        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        finish()
    }
}