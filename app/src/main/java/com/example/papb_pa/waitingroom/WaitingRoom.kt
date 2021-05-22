package com.example.papb_pa.waitingroom

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papb_pa.Game.Maen
import com.example.papb_pa.MainActivity
import com.google.firebase.database.*
import com.example.papb_pa.R
import com.example.papb_pa.data.User
import kotlinx.android.synthetic.main.activity_waiting_room.*
import java.util.*

class WaitingRoom : AppCompatActivity() {
    private lateinit var code : String
    private lateinit var jeneng : String
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        maen_load.visibility = View.GONE
        var user = arrayListOf<User>()
        val heroAdapter = HeroAdapter(user)
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        code=intent.getStringExtra("code").toString()
        jeneng = intent.getStringExtra("jeneng").toString()
        val ref = database.reference.child("room").child(code)
        showCode.text = code
        val refUser = ref.child("user")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user.clear()
                for (userSnapshot in dataSnapshot.children) {
                    user.add(
                        User(
                            userSnapshot.child("id").getValue().toString(),
                            userSnapshot.child("jeneng").getValue().toString()
                        )
                    )
                }
                if (user.size>0){
                    load_player.visibility = View.GONE
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
            bt_wr_maen.isEnabled = false
            bt_wr_maen.visibility = View.GONE
            maen_load.visibility = View.VISIBLE
            var i = 0;
            refUser.get().addOnSuccessListener { snapshot ->
                if (snapshot.childrenCount > 1){
                    var soal = arrayListOf<String>()
                    database.reference.child("soal").get().addOnSuccessListener { value ->
                        value.children.forEach { dataSnapshot ->
                            soal.add(dataSnapshot.value.toString())
                        }
                        soal.shuffle()
                        ref.child("soal").get().addOnSuccessListener {
                            if (!it.exists()){
                                ref.child("soal").setValue(soal.subList(0,user.size))
                            }
                        }
                    }
                    snapshot.children.forEach { user ->
                        i++
                        refUser.child(user.key.toString()).child("numb").setValue(i)
                        refUser.child(user.key.toString()).child("poin").setValue(0)
                    }

                    var roomRef = database.reference.child("room").child(code)
                    roomRef.child("maen").setValue(true)
                    roomRef.child("round").setValue(1)
                    roomRef.child("numb").setValue(1)
                } else{
                    val builder = AlertDialog.Builder(this@WaitingRoom)
                    builder.setTitle("Wara-wara")
                    builder.setMessage("Pemaine kurang, entenono sek")
                    builder.setPositiveButton("nggeh") { dialog, which ->
                        bt_wr_maen.isEnabled = true
                        bt_wr_maen.visibility = View.VISIBLE
                        maen_load.visibility = View.GONE
                    }
                    builder.show()
                }
            }
        }
        val maenListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()){
                    var intent = Intent(this@WaitingRoom, Maen::class.java)
                    intent.putExtra("code", code)
                    intent.putExtra("jeneng", jeneng)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@WaitingRoom.finish()
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

    override fun onBackPressed() {
        super.onBackPressed()
        database.reference.child("room").child(code).removeValue()
    }
}