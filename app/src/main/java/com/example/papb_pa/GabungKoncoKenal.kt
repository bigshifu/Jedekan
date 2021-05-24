package com.example.papb_pa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.example.papb_pa.data.User
import com.example.papb_pa.waitingroom.WaitingRoom
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_gabung_konco_kenal.*

class GabungKoncoKenal : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gabung_konco_kenal)
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        var code = ""
        var jeneng = ""
        bt_gkl_gabung.setOnClickListener {
            code = et_code_gk.text.toString()
            jeneng = et_gkl_jeneng.text.toString()
            var ref = database.reference.child("room").child(code)
            ref.get().addOnSuccessListener {
                if (!it.exists()){
                    Toast.makeText(this, "Code tidak terdaftar!!", Toast.LENGTH_LONG).show()
                }else{
                    var nwUser = User(id, jeneng)
                    ref.child("user").child(id).setValue(nwUser).addOnSuccessListener {
                        var intent = Intent(this, WaitingRoom::class.java)
                        intent.putExtra("code", code)
                        intent.putExtra("jeneng", jeneng)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}