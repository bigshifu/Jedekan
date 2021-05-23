package com.example.papb_pa.Game

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papb_pa.Logout
import com.example.papb_pa.R
import com.example.papb_pa.data.Pesan
import com.example.papb_pa.data.User
import com.example.papb_pa.waitingroom.HeroAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_gambar.view.*
import kotlinx.android.synthetic.main.fragment_jawab.*
import kotlinx.android.synthetic.main.fragment_jawab.view.*
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [Jawab.newInstance] factory method to
 * create an instance of this fragment.
 */
class Jawab : Fragment() {
    // TODO: Rename and change types of parameters
    private var id : String? = null
    private  var code : String? = null
    private var jeneng : String? = null
    private val database = FirebaseDatabase.getInstance("https://jedekan-gambar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    var soal = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
            code = it.getString(ARG_PARAM2)
            jeneng = it.getString(ARG_PARAM3)
        }
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        et_pesan.requestFocus()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_jawab, container, false)
        setNamaPemain(view)
        database.reference.child("soal").get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach {
                soal.add(it.value.toString())
            }
        }

        view.bt_send.setOnClickListener {
            onSend(view)
        }
        view.IB_FJ_keluar.setOnClickListener {
            val i = Intent(activity, Logout::class.java)
            startActivity(i)
        }
        view.et_pesan.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                //Perform Code
                onSend(view)
                return@OnKeyListener true
            }
            false
        })
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                var time = dataSnapshot.child("timer").value.toString()
                var numb = dataSnapshot.child("numb").value.toString()
                var bm = dataSnapshot.child("gambar").child(numb).value.toString()
                if (time != "null" && time !=""){
                    view.jawab_timer.setProgress(Integer.parseInt(time))
                }
                if (bm != "null" && bm !=""){
                    view.gambar_realtime.setImageBitmap(convert(bm))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(view.context, "Fail to Load Data", Toast.LENGTH_SHORT).show()
            }
        }
        database.reference.child("room").child(code.toString()).addValueEventListener(postListener)
        getPesan(view)
        return view
    }



    private fun onSend(view: View){
        var pesan = view.et_pesan.text.toString()
        view.et_pesan.text.clear()
        var refPoin = database.reference.child("room")
            .child(code.toString()).child("user")
            .child(id.toString())
            .child("poin")
        if (soal.contains(pesan.toLowerCase())){
            sendPesan("Jawabane $jeneng bener")
            refPoin.get().addOnSuccessListener {
                refPoin.setValue(Integer.parseInt(it.value.toString()) + 10)
            }
        }else{
            sendPesan("Jawabane $jeneng salah")
            refPoin.get().addOnSuccessListener {
                refPoin.setValue(Integer.parseInt(it.value.toString()) - 3)
            }
        }
    }

    private fun convert(base64Str: String): Bitmap {
        val decodedBytes: ByteArray = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun sendPesan(pesan: String){
        val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        val now = Date()
        val strDate: String = sdfDate.format(now)
        var master = Pesan(id, jeneng, pesan.toString())
        database.reference.child("room")
                .child(code.toString()).child("pesan")
                .child(strDate)
                .setValue(master)
    }

    private fun getPesan(view: View){
        var pesan = arrayListOf<Pesan>()
        val pesanAdapter = PesanAdapter(pesan)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                pesan.clear()
                for (userSnapshot in dataSnapshot.children) {
                    pesan.add(
                        Pesan(
                            userSnapshot.child("id").value.toString(),
                            userSnapshot.child("jeneng").value.toString(),
                            userSnapshot.child("pesan").value.toString()
                        )
                    )
                }
                pesanAdapter.notifyDataSetChanged()
                if (pesan.size>0){
                    view.RV_pesan.smoothScrollToPosition(pesan.size - 1)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(view.context, "Fail to Load Data", Toast.LENGTH_SHORT).show()
            }
        }
        val ref = database.reference.child("room").child(code.toString()).child("pesan")
        ref.addValueEventListener(postListener)
        view.RV_pesan.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = pesanAdapter
        }
    }
    private fun setNamaPemain(view: View){
        var user = arrayListOf<User>()
        val heroAdapter = HeroAdapter(user)
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
                heroAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(view.context, "Fail to Load Data", Toast.LENGTH_SHORT).show()
            }
        }
        val ref = database.reference.child("room").child(code.toString()).child("user")
        ref.addValueEventListener(postListener)
        view.RV_namaPemain.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = heroAdapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Jawab.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            Jawab().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }
}