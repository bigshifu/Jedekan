package com.example.papb_pa.GabungWongLiyo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papb_pa.R
import com.example.papb_pa.data.getRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_gabung_wong_liyo.*
import kotlinx.android.synthetic.main.activity_waiting_room.*

class GabungWongLiyo  : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gabung_wong_liyo)
        var room = arrayListOf<getRoom>()
        val roomAdapter = RoomAdapter(room)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                room.clear()
                for (roomSnapshot in dataSnapshot.children) {
                    if (!roomSnapshot.hasChild("maen")){
                        room.add(
                                getRoom(
                                        roomSnapshot.child("code").getValue().toString(),
                                        roomSnapshot.child("id").getValue().toString(),
                                        roomSnapshot.child("jeneng").getValue().toString(),
                                        roomSnapshot.child("user").childrenCount.toInt()
                                )
                        )
                    }
                }
                roomAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@GabungWongLiyo,"Fail to Load Data", Toast.LENGTH_SHORT).show()
            }
        }
        val ref = database.reference.child("room")
        ref.addValueEventListener(postListener)

        rv_room.apply {
            layoutManager = LinearLayoutManager(this@GabungWongLiyo)
            adapter = roomAdapter
        }
    }
}