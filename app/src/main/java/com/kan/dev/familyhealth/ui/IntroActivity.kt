package com.kan.dev.familyhealth.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kan.dev.familyhealth.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d("Kan","onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Kan","onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Kan","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Kan","onStop")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("Kan","onDestroy")
    }
}