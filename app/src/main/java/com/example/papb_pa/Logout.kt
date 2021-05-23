package com.example.papb_pa


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Toast
import com.example.papb_pa.data.User
import com.example.papb_pa.waitingroom.HeroAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_logout.*
import kotlinx.android.synthetic.main.fragment_jawab.*
import kotlinx.android.synthetic.main.fragment_jawab.view.*
import java.util.*
import kotlin.system.exitProcess

class Logout : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    lateinit var id : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        btn_iyo.setOnClickListener{
            finishAffinity()
            exitProcess(0)
        }

        btn_nggak.setOnClickListener{
            finish()
        }

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val lebar = dm.widthPixels
        val tinggi = dm.heightPixels

        window.setLayout(((lebar*.6).toInt()), ((tinggi*.4).toInt()))

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = -20
        window.attributes = params
    }

}