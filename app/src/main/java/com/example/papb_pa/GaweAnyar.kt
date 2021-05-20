package com.example.papb_pa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gawe_anyar)
        maen_anyar_load.visibility = View.GONE
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        var code = (1..STRING_LENGTH).map {
                i -> kotlin.random.Random.nextInt(0, charPool.size)
        }.map(charPool::get).joinToString("")
        TV_code.text = code
        var jeneng = "Paijo"
        bt_ga_maen.setOnClickListener {
            bt_ga_maen.isEnabled = false
            bt_ga_maen.visibility = View.GONE
            maen_anyar_load.visibility = View.VISIBLE
            jeneng = et_ga_jeneng.text.toString()
            var value = storeRoom(code, id, jeneng)
            database.reference.child("room").child(code).setValue(value).addOnCompleteListener {
                var master = User(id, jeneng)
                database.reference.child("room").child(code).child("user").child(id).setValue(master).addOnCompleteListener {
                        var intent = Intent(this, WaitingRoom::class.java)
                        intent.putExtra("code", code)
                        intent.putExtra("jeneng", jeneng)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        this@GaweAnyar.finish()
                }
            }
        }
    }
}