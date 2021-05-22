package com.example.papb_pa

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.papb_pa.GabungWongLiyo.GabungWongLiyo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedpreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedpreference = getSharedPreferences("preference", Context.MODE_PRIVATE)

        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        val editor: SharedPreferences.Editor = sharedpreference.edit()
        editor.putString("id",id)
        editor.apply()

        btnNewGame.setOnClickListener {
            var intent = Intent(this, GaweAnyar::class.java)
            startActivity(intent)
        }
        btnJoinGame.setOnClickListener {
            var intent = Intent(this, GabungWongLiyo::class.java)
            startActivity(intent)
        }
        btnJoinFriend.setOnClickListener {
            var intent = Intent(this, GabungKoncoKenal::class.java)
            startActivity(intent)
        }
    }
}