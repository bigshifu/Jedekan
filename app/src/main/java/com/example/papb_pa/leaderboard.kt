package com.example.papb_pa

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papb_pa.GabungWongLiyo.RoomAdapter
import com.example.papb_pa.data.User
import com.example.papb_pa.data.getRoom
import com.example.papb_pa.waitingroom.HeroAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_gabung_wong_liyo.*
import kotlinx.android.synthetic.main.activity_leaderboard.*

class leaderboard : AppCompatActivity() {
    private var code = ""
    private var id = ""
    private lateinit var sharedpreference : SharedPreferences
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        var user = arrayListOf<User>()
        val userAdapter = HeroAdapter(user)
        sharedpreference = getSharedPreferences("preference", Context.MODE_PRIVATE)
        id = sharedpreference.getString("id","").toString()
        code =intent.getStringExtra("code").toString()
        database.reference.child("room").child(code).child("user").orderByChild("poin").get().addOnSuccessListener { snapshot ->
            load_rank.visibility = View.GONE
            snapshot.children.forEach { data ->
                user.add(
                    User(
                        data.child("id").getValue().toString(),
                        data.child("jeneng").getValue().toString()
                    )
                )
            }
            database.reference.child("room").child(code).child("user").get().addOnSuccessListener {
                if (it.exists()){
                    if (it.childrenCount < 1){
                        database.reference.child("room").child(code).removeValue()
                    }else{
                        database.reference.child("room").child(code).child("user").child(id).removeValue()
                    }
                }
            }
        }

        rv_peringkat.apply {
            layoutManager = LinearLayoutManager(this@leaderboard)
            adapter = userAdapter
        }
    }
}