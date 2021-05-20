package com.example.papb_pa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.papb_pa.GabungWongLiyo.GabungWongLiyo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnNewGame.setOnClickListener {
            var intent = Intent(this, GaweAnyar::class.java)
            startActivity(intent)
        }
        btnJoinGame.setOnClickListener {
            var intent = Intent(this, GabungWongLiyo::class.java)
            startActivity(intent)
        }
    }
}