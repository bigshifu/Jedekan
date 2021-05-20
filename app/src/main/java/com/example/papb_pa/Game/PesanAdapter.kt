package com.example.papb_pa.Game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.papb_pa.R
import com.example.papb_pa.data.Pesan
import kotlinx.android.synthetic.main.item_chat.view.*

class PesanAdapter(private val pesan: List<Pesan>) : RecyclerView.Adapter<PesanHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PesanHolder {
        return PesanHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat, viewGroup, false))
    }

    override fun getItemCount(): Int = pesan.size

    override fun onBindViewHolder(holder: PesanHolder, position: Int) {
        holder.bindPesan(pesan[position])
    }
}

class PesanHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tvHeroName = view.tv_nama
    private val tvPesan = view.tv_pesan

    fun bindPesan(pesan: Pesan) {
        tvHeroName.text = pesan.jeneng
        tvPesan.text = pesan.pesan
    }
}