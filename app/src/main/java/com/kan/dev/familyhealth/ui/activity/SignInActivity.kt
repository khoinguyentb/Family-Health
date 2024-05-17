package com.kan.dev.familyhealth.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySignInBinding
import com.kan.dev.familyhealth.utils.LOG_APP
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick


class SignInActivity : BaseActivity<ActivitySignInBinding>() {
    override fun setViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var intent: Intent
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
                    intent = Intent(this@SignInActivity,SignUpActivity::class.java)
                    startActivity(intent)
                    handler.postDelayed({ isClick = true},500)
                }
            }
        }
    }

    private fun signIn(email:String,pass : String){
        dialog.show()
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(
                this,
                OnCompleteListener<AuthResult?> { task ->
                    dialog.dismiss()
                    if (task.isSuccessful) {
//                        if (sharePre.getBoolean(LOG_APP,false)){
//                            intent = Intent(this@SignInActivity,MainActivity::class.java)
//                        }else{
//                            intent = Intent(this@SignInActivity,InformationActivity::class.java)
//                        }
                        intent = Intent(this@SignInActivity,InformationActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignInActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }
}