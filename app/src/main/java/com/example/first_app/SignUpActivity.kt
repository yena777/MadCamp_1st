package com.example.first_app

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.first_app.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var mBinding : ActivitySignUpBinding ?= null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize Firebase Auth
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val joinBtn = findViewById<Button>(R.id.btn_join)
        joinBtn.setOnClickListener{
            val email = binding.etEmail
            val pwd = binding.etPassword
            auth.createUserWithEmailAndPassword(email.text.toString().trim(), pwd.text.toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Authentication succeed.", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                        //val user = auth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        // 현재 로그인 상태
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Toast.makeText(this, "Login succeed", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        // 새로 로그인 or 회원가입
        binding.btnLogin.setOnClickListener{
            val email = binding.etEmail
            val pwd = binding.etPassword
            auth.signInWithEmailAndPassword(email.text.toString().trim(), pwd.text.toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login succeed", Toast.LENGTH_SHORT).show()
                        //Toast.makeText(this, auth.currentUser?.uid.toString(), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "SignIn failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}