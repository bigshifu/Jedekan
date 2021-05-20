package com.example.papb_pa.Game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.papb_pa.MainActivity
import com.google.firebase.database.*
import com.example.papb_pa.R
import kotlinx.android.synthetic.main.activity_maen_game.*
import kotlinx.android.synthetic.main.fragment_jawab.*
import kotlinx.android.synthetic.main.view_gambar.*

class Maen : AppCompatActivity() {
    private var time = 0
    private var code = ""
    var bgThread = Thread()
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maen_game)
        code = intent.getStringExtra("code").toString()
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        var ref = database.reference
            .child("room")
            .child(code)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var roomNumb = dataSnapshot.child("numb").value.toString()
                var playerName = dataSnapshot.child("user").child(id).child("jeneng").value.toString()
                var playerNumb = dataSnapshot.child("user").child(id).child("numb").value.toString()
                var idRoom = dataSnapshot.child("id").value.toString()
                if (idRoom==id && !dataSnapshot.hasChild("timer")){
                    if (bgThread.state == Thread.State.NEW){
                        bgThread = Thread(runnable())
                        bgThread.start()
                    }
                }
                if (playerNumb==roomNumb ){
                    if (supportFragmentManager.findFragmentByTag("gambar")==null){
                        var fragGambar = gambar.newInstance(id, code)
                        supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_maen, fragGambar, "gambar")
                                .commitNow()
                    }else{
                        supportFragmentManager.findFragmentByTag("jawab")?.let {
                            supportFragmentManager.beginTransaction().hide(it).commitNow()
                        }
                        supportFragmentManager.findFragmentByTag("gambar")?.let {
                            supportFragmentManager.beginTransaction().show(it).commitNow()
                        }
                    }
                }else if(supportFragmentManager.findFragmentByTag("jawab")==null){
                    var fragJawab = Jawab.newInstance(id, code, playerName)
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_maen, fragJawab, "jawab")
                        .commitNow()
                }else{
                    supportFragmentManager.findFragmentByTag("gambar")?.let {
                        supportFragmentManager.beginTransaction().hide(it).commitNow()
                    }
                    supportFragmentManager.findFragmentByTag("jawab")?.let {
                        supportFragmentManager.beginTransaction().show(it).commitNow()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        ref.addValueEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        bgThread.interrupt()
    }

    inner class runnable: Runnable {
        var ref = database.reference
                .child("room")
                .child(code)
        override fun run() {
            try {
                while (true){
                    Thread.sleep(1000)
                    time++
                    ref.child("timer").setValue(time)
                    if (time<60){

                    }else{
                        time=0
                        ref.get().addOnSuccessListener {
                            var numb = it.child("numb").value.toString()
                            var round = it.child("round").value.toString()
                            var userSize = it.child("user").childrenCount
                            if (Integer.parseInt(numb)<userSize){
                                time=0
                                ref.child("numb").setValue(Integer.parseInt(numb)+1)
                            }else if(Integer.parseInt(round)<3){
                                time=0
                                ref.child("round").setValue(Integer.parseInt(round)+1)
                                ref.child("numb").setValue(1)
                                ref.child("gambar").setValue("")
                            }else{
                                bgThread.interrupt()
                                var intent = Intent(this@Maen, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }catch (e : InterruptedException){
                e.printStackTrace()
            }
        }
    }
}