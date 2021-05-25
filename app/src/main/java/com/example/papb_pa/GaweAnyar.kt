package com.example.papb_pa

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.View
import com.example.papb_pa.data.User
import com.example.papb_pa.data.storeRoom
import com.example.papb_pa.waitingroom.WaitingRoom
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_gawe_anyar.*

class GaweAnyar : AppCompatActivity() {
    private val STRING_LENGTH = 4
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
    private lateinit var sharedpreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gawe_anyar)

        sharedpreference = getSharedPreferences("preference", Context.MODE_PRIVATE)

        val id = sharedpreference.getString("id","")
        bt_ga_maen.isEnabled = false
        database.reference.child("room").get().addOnSuccessListener {

            do {
                var setCode = randomCode()
                TV_code.text = setCode
            }while (it.hasChild(setCode))
            bt_ga_maen.isEnabled = true
            var jeneng = "Paijo"
            var code = TV_code.text.toString()
            bt_ga_maen.setOnClickListener {
                var et = et_ga_jeneng.text.toString()
                if (et != ""){
                    jeneng = et
                }
                var value = storeRoom(code, id, jeneng)
                database.reference.child("room").child(code).setValue(value).addOnCompleteListener {
                    var master = User(id, jeneng)
                    database.reference.child("room").child(code).child("user").child(id.toString()).setValue(master).addOnCompleteListener {
                        var intent = Intent(this, WaitingRoom::class.java)
                        intent.putExtra("code", code)
                        intent.putExtra("jeneng", jeneng)
                        startActivity(intent)
                    }
                }
            }
        }
    }
    fun randomCode(): String {
        return (1..STRING_LENGTH).map {
                i -> kotlin.random.Random.nextInt(0, charPool.size)
        }.map(charPool::get).joinToString("")
    }
}