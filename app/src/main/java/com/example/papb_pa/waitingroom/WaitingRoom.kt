package com.example.papb_pa.waitingroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papb_pa.Game.Maen
import com.google.firebase.database.*
import com.example.papb_pa.R
import com.example.papb_pa.data.User
import kotlinx.android.synthetic.main.activity_waiting_room.*
import java.util.*

class WaitingRoom : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        var user = arrayListOf<User>()
        val heroAdapter = HeroAdapter(user)
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        var numUser = 0
        val code=intent.getStringExtra("code").toString()
        val ref = database.reference.child("room").child(code)
        val refUser = ref.child("user")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user.clear()
                numUser = dataSnapshot.childrenCount.toInt()
                for (userSnapshot in dataSnapshot.children) {
                    user.add(
                        User(
                            userSnapshot.child("id").getValue().toString(),
                            userSnapshot.child("jeneng").getValue().toString()
                        )
                    )
                }
                heroAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@WaitingRoom,"Fail to Load Data", Toast.LENGTH_SHORT).show()
            }
        }
        refUser.addValueEventListener(postListener)
        database.reference.child("room").child(intent.getStringExtra("code").toString()).child("id").get().addOnSuccessListener {
            if (it.value.toString() == id.toString()){
                bt_wr_maen.visibility = View.VISIBLE
            }else{
                bt_wr_maen.visibility = View.GONE
            }
        }

        bt_wr_maen.setOnClickListener {
            var i = 0;
            var soal = arrayListOf<String>()
            database.reference.child("soal").get().addOnSuccessListener {
                it.children.forEach { dataSnapshot ->
                    soal.add(dataSnapshot.value.toString())
                }
                soal.shuffle()
                ref.child("soal").get().addOnSuccessListener {
                    if (!it.exists()){
                        ref.child("soal").setValue(soal.subList(0,user.size))
                    }
                }
            }
            refUser.get().addOnSuccessListener {
                    it.children.forEach { user ->
                        i++
                        refUser.child(user.key.toString()).child("numb").setValue(i)
                        refUser.child(user.key.toString()).child("poin").setValue(0)
                    }
                }
            var roomRef = database.reference.child("room").child(intent.getStringExtra("code").toString())
            roomRef.child("maen").setValue(true)
            roomRef.child("round").setValue(1)
            roomRef.child("numb").setValue(1)
        }
        val maenListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()){
                    var intent = Intent(this@WaitingRoom, Maen::class.java)
                    intent.putExtra("code", code)
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@WaitingRoom,"Fail to Load Data", Toast.LENGTH_SHORT).show()
            }
        }
        database.reference.child("room").child(intent.getStringExtra("code").toString()).child("maen").addValueEventListener(maenListener)

        rvWaitingroom.apply {
            layoutManager = LinearLayoutManager(this@WaitingRoom)
            adapter = heroAdapter
        }
    }
}