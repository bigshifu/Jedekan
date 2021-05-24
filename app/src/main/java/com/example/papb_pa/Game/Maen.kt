package com.example.papb_pa.Game

import android.app.Activity
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.papb_pa.*
import com.example.papb_pa.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maen_game.*
import kotlinx.android.synthetic.main.fragment_jawab.*
import kotlinx.android.synthetic.main.view_gambar.*
import java.text.SimpleDateFormat
import java.time.Instant.now
import java.util.*

class Maen : AppCompatActivity() {
    companion object{
        lateinit var maen : Activity
    }
    private var time = 0
    private var code = ""
    private var jeneng = ""
    private var id = ""
    private var bgThread = Thread()
    private lateinit var fragGambar : Fragment
    private lateinit var fragJawab: Fragment
    private lateinit var sharedpreference : SharedPreferences
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maen_game)
        maen = this@Maen
        code = intent.getStringExtra("code").toString()
        jeneng = intent.getStringExtra("jeneng").toString()
        sharedpreference = getSharedPreferences("preference", Context.MODE_PRIVATE)
        id = sharedpreference.getString("id","").toString()
        var ref = database.reference
            .child("room")
            .child(code)
        fragGambar = gambar.newInstance(id, code)
        fragJawab = Jawab.newInstance(id, code, jeneng)
        if (savedInstanceState!=null){
            supportFragmentManager.getFragment(savedInstanceState, "gambar")?.let {
                fragGambar = it
            }
            supportFragmentManager.getFragment(savedInstanceState, "jawab")?.let {
                fragJawab = it
            }
        }else{
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_maen, fragGambar, "gambar")
                .commitNow()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_maen, fragJawab, "jawab")
                .commitNow()
        }
        var second : Long = 15000
        val builder = AlertDialog.Builder(this@Maen)
        builder.setTitle("Wara-wara")
        builder.setMessage("Koneksine seng gawe room gak stabil, buyar ae yo!")
        builder.setPositiveButton("nggeh") { dialog, which ->
            var intent = Intent(this@Maen, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this@Maen.finish()
        }
        val timer = object: CountDownTimer(second, 1000) {
            override fun onTick(p0: Long) {

            }
            override fun onFinish() {
                if (!this@Maen.isFinishing) {
                    builder.show()
                }
            }
        }
        timer.start()
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                timer.cancel()
                second = 15000
                timer.start()
                var maen = dataSnapshot.child("maen").value.toString()
                var roomNumb = dataSnapshot.child("numb").value.toString()
                var playerNumb = dataSnapshot.child("user").child(id).child("numb").value.toString()
                var idRoom = dataSnapshot.child("id").value.toString()
                if (idRoom==id && !dataSnapshot.hasChild("timer")){
                    if (bgThread.state == Thread.State.NEW){
                        bgThread = Thread(runnable())
                        bgThread.start()
                    }
                }
                if (maen == "false"){
                    if (this@Maen.isDestroyed){
                        var intent = Intent(this@Maen, leaderboard::class.java)
                        intent.putExtra("code", code)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        this@Maen.finish()
                    }
                }
                if (playerNumb==roomNumb ){
                    if (!this@Maen.isDestroyed){
                        supportFragmentManager.beginTransaction().hide(fragJawab).commitNowAllowingStateLoss()
                        supportFragmentManager.beginTransaction().show(fragGambar).commitNowAllowingStateLoss()
                    }
                }else{
                    if (!this@Maen.isDestroyed){
                        supportFragmentManager.beginTransaction().hide(fragGambar).commitNowAllowingStateLoss()
                        supportFragmentManager.beginTransaction().show(fragJawab).commitNowAllowingStateLoss()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        ref.addValueEventListener(listener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (fragJawab.isAdded){
            supportFragmentManager.putFragment(outState, "jawab", fragJawab)
        }
        if (fragGambar.isAdded){
            supportFragmentManager.putFragment(outState, "gambar", fragGambar)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val joinIntent = Intent(this, Notif::class.java).apply {
            this.putExtra("action", "join")
            this.putExtra("code", code)
            this.putExtra("jeneng", jeneng)
            this.putExtra("id", id)
        }
        val exitIntent = Intent(this, Notif::class.java).apply {
            this.putExtra("action", "exit")
        }
        val joinPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 1, joinIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val exitPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 2, exitIntent, 0)
        val builder = NotificationCompat.Builder(this, "running")
            .setSmallIcon(R.drawable.gambarmenu)
            .setContentTitle("Wara-wara")
            .setContentText("Gamemu sek mlaku")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.keluar, "Join", joinPendingIntent)
            .addAction(R.drawable.keluar, "Exit", exitPendingIntent)
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(), 0))
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {

            // notificationId is a unique int for each notification that you must define

            notify(1, builder.build())

        }

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

                    val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")

                    val now = Date()
                    val strDate: String = sdfDate.format(now)
                    ref.child("update").setValue(strDate)
                    if (time>=60){
                        time=0
                        ref.get().addOnSuccessListener {
                            var numb = it.child("numb").value.toString()
                            var round = it.child("round").value.toString()
                            var userSize = it.child("user").childrenCount
                            if (Integer.parseInt(numb)<userSize){
                                time=0
                                ref.child("numb").setValue(Integer.parseInt(numb)+1)
                            }else if(Integer.parseInt(round)<2){
                                time=0
                                ref.child("round").setValue(Integer.parseInt(round)+1)

                                ref.child("numb").setValue(1)
                            }else{
                                bgThread.interrupt()

                                ref.child("maen").setValue("false")
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