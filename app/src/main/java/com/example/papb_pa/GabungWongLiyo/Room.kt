package com.example.papb_pa.GabungWongLiyo


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.papb_pa.R
import com.example.papb_pa.data.User
import com.example.papb_pa.data.getRoom
import com.example.papb_pa.waitingroom.WaitingRoom
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.rowview2.view.*


class RoomAdapter(private val room: List<getRoom>) : RecyclerView.Adapter<RoomAdapter.RoomHolder>() {
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RoomHolder {
        return RoomHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.rowview2,
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount(): Int = room.size

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        holder.bindHero(room[position])
        holder.itemView.setOnClickListener {
            val context=holder.title
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            database.reference.child("room")
                .child(room[position].code.toString())
                .child("user")
                .child(id).get().addOnSuccessListener {
                    if (it.exists()){
                        var intent = Intent(context, WaitingRoom::class.java)
                        intent.putExtra("code", room[position].code.toString())
                        context.startActivity(intent)
                    }else{
                        val alert: AlertDialog.Builder = AlertDialog.Builder(context)
                        alert.setTitle("Jenengmu")
                        // Set an EditText view to get user input
                        val input = EditText(context)
                        alert.setView(input)

                        alert.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, whichButton ->
                            // Do something with value!
                            var master = User(id, input.text.toString())
                            database.reference.child("room")
                                .child(room[position].code.toString())
                                .child("user")
                                .child(id)
                                .setValue(master)
                                .addOnCompleteListener {
                                    var intent = Intent(context, WaitingRoom::class.java)
                                    intent.putExtra("code", room[position].code.toString())
                                    intent.putExtra("jeneng", input.text.toString())
                                    context.startActivity(intent)
                                }
                        })

                        alert.setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, whichButton ->
                                // Canceled.
                            })

                        alert.show()
                    }
                }
        }
    }
    inner class RoomHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.context
        private val jeneng = view.jeneng_room
        private val number = view.numplayer
        private val container = view.room_player_container

        fun bindHero(room: getRoom) {
            jeneng.text = room.jeneng
            number.text = room.jumlah.toString()+"/10"
        }
    }

}

