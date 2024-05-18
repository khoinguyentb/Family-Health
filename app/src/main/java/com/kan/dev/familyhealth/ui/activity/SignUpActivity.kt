package com.kan.dev.familyhealth.ui.activity

import android.R.attr.password
import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySignUpBinding


class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {
    override fun setViewBinding(): ActivitySignUpBinding {
        return ActivitySignUpBinding.inflate(layoutInflater)
    }
    private val dialog : ProgressDialog by lazy {
        ProgressDialog(this)
    }

    private lateinit var auth: FirebaseAuth
    private var phoneNumber : String = ""
    private var email : String = ""
    private var pass : String = ""
    private var conFirmPass : String = ""
    private var userName : String = ""

    override fun initData() {
        auth = FirebaseAuth.getInstance()
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.apply {
            btnSignUp.setOnClickListener {
                phoneNumber = edtPhone.text.toString()
                email = edtEmail.text.toString()
                pass = edtPass.text.toString()
                conFirmPass = edtConFirmPass.text.toString()
                userName = edtUserName.text.toString()
                if (isValueDate()){
                    setUser(email,pass)
                }
            }
            imgBack.setOnClickListener {
                finish()
            }
            btnSignIn.setOnClickListener {
                finish()
            }
        }
    }

    private fun setUser(email : String,pass : String){
        dialog.show()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(
                this,
                OnCompleteListener<AuthResult?> { task ->
                    dialog.dismiss()
                    if (task.isSuccessful) {
                        Log.d("KanMobile", "createUserWithEmail:success")
                        Toast.makeText(this,getString(R.string.SignUpSuccess),Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this,getString(R.string.SignUpFailed),Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun isValueDate() :Boolean {
        binding.apply {
            return if (userName == "" || phoneNumber == "" || email == "" || pass == "" || conFirmPass == ""){
                Toast.makeText(this@SignUpActivity,getString(R.string.NoEmpty),Toast.LENGTH_SHORT).show()
                false
            }else{
                if (pass == conFirmPass){
                    true
                }else{
                    Toast.makeText(this@SignUpActivity,getString(R.string.Passwords_do_not_match),Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
        return true
    }
}