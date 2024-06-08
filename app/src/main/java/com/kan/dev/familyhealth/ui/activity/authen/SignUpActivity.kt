package com.kan.dev.familyhealth.ui.activity.authen

import android.app.ProgressDialog
import android.text.InputType
import android.util.Log
import android.util.Patterns
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
    private var email : String = ""
    private var pass : String = ""
    private var conFirmPass : String = ""
    private var isPasswordVisible: Boolean = false
    private var isPasswordConfirmVisible: Boolean = false
    override fun initData() {
        auth = FirebaseAuth.getInstance()
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.apply {
            btnSignUp.setOnClickListener {
                email = edtEmail.text.toString()
                pass = edtPass.text.toString()
                conFirmPass = edtConFirmPass.text.toString()
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

            icShowPass.setOnClickListener {
                if (isPasswordVisible) {
                    edtPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    icShowPass.setImageResource(R.drawable.hide_pass)
                } else {
                    edtPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    icShowPass.setImageResource(R.drawable.show_pass)
                }
                edtPass.setSelection(edtPass.text.length)
                isPasswordVisible = !isPasswordVisible
            }

            icShowPassConfirm.setOnClickListener {
                if (isPasswordConfirmVisible) {
                    edtConFirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    icShowPassConfirm.setImageResource(R.drawable.hide_pass)
                } else {
                    edtConFirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    icShowPassConfirm.setImageResource(R.drawable.show_pass)
                }
                edtConFirmPass.setSelection(edtConFirmPass.text.length)
                isPasswordConfirmVisible = !isPasswordConfirmVisible
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
                        Toast.makeText(this,getString(R.string.EmailAlready),Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun isValueDate() :Boolean {
        binding.apply {
            return if (email == "" || pass == "" || conFirmPass == ""){
                Toast.makeText(this@SignUpActivity,getString(R.string.NoEmpty),Toast.LENGTH_SHORT).show()
                false
            }else{
                if (pass == conFirmPass){
                    true
                }else{
                    Toast.makeText(this@SignUpActivity,getString(R.string.Passwords_do_not_match),Toast.LENGTH_SHORT).show()
                    false
                }
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    true
                }else{
                    Toast.makeText(this@SignUpActivity,getString(R.string.EmailValid),Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
        return true
    }
}