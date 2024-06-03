package com.kan.dev.familyhealth.ui.activity.authen

import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySignInBinding
import com.kan.dev.familyhealth.ui.activity.main.MainActivity
import com.kan.dev.familyhealth.ui.activity.interaction.InformationActivity
import com.kan.dev.familyhealth.utils.Code
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick


class SignInActivity : BaseActivity<ActivitySignInBinding>() {
    override fun setViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var intent: Intent
    private lateinit var user : FirebaseUser
    private var email : String = ""
    private var pass : String = ""
    private val dialog : ProgressDialog by lazy {
        ProgressDialog(this)
    }
    override fun initData() {
        auth = FirebaseAuth.getInstance()
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.apply {
            btnSignIn.setOnClickListener {
                email = edtUserName.text.toString()
                pass = edtPass.text.toString()
                if (isClick){
                    isClick = false
                    signIn(email,pass)
                    handler.postDelayed({ isClick = true},500)
                }
            }
            btnSignUp.setOnClickListener {
                if (isClick){
                    isClick = false
                    intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                    startActivity(intent)
                    handler.postDelayed({ isClick = true},500)
                }
            }
        }
    }

    private fun signIn(email:String,pass : String){
        if (email.trim() == "" || pass.trim() == ""){
            Toast.makeText(
                this@SignInActivity, getString(R.string.ToastSignIn),
                Toast.LENGTH_SHORT
            ).show()
        }else{
            dialog.show()
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(
                    this,
                    OnCompleteListener<AuthResult?> { task ->
                        dialog.dismiss()
                        if (task.isSuccessful) {
                            user = FirebaseAuth.getInstance().currentUser!!
                            Log.d("KanMobile",user.displayName.toString())
                            intent = if (user.displayName != null){
                                sharePre.putString(MY_CODE,user.displayName)
                                Code = user.displayName!!
                                Intent(this@SignInActivity, MainActivity::class.java)
                            }else{
                                Intent(this@SignInActivity, InformationActivity::class.java)
                            }
                            Toast.makeText(
                                this@SignInActivity, getString(R.string.LoggedSuccessfully),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignInActivity, getString(R.string.IncorrectSign),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }
}