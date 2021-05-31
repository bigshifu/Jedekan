package com.example.papb_pa

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.papb_pa.Game.Maen
import com.google.firebase.database.FirebaseDatabase


class Notif : BroadcastReceiver() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action: String = p1?.getStringExtra("action").toString()
        val code : String = p1?.getStringExtra("code").toString()
        val jeneng : String = p1?.getStringExtra("jeneng").toString()
        val id : String = p1?.getStringExtra("id").toString()
        if (action == "join") {
            p0?.let { NotificationManagerCompat.from(it).cancel(1) }
            var intent = Intent(p0, Maen::class.java)
            intent.putExtra("code", code)
            intent.putExtra("jeneng", jeneng)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            p0?.startActivity(intent)
        } else if (action == "exit") {
            p0?.let { NotificationManagerCompat.from(it).cancel(1) }
            p1?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            Maen.maen.finish()
            database.reference.child("room").child(code).child("user").child(id).removeValue()
        }
    }
}