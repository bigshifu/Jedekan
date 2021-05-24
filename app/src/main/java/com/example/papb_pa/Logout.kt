package com.example.papb_pa

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_logout.*
import kotlin.system.exitProcess

class Logout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        btn_iyo.setOnClickListener {
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