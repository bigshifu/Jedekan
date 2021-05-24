package com.example.papb_pa

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_logout.*
import kotlin.system.exitProcess

class Logout : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        var code = intent.getStringExtra("code").toString()
        var id = intent.getStringExtra("id").toString()
        btn_iyo.setOnClickListener {
            database.reference.child("room").child(code).child("user").child(id).removeValue()
            finishAffinity()
            exitProcess(0)
        }
            btn_nggak.setOnClickListener {
                finish()
            }

            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)

            val lebar = dm.widthPixels
            val tinggi = dm.heightPixels

            window.setLayout(((lebar * .6).toInt()), ((tinggi * .4).toInt()))

            val params = window.attributes
            params.gravity = Gravity.CENTER
            params.x = 0
            params.y = -20
            window.attributes = params
    }
}