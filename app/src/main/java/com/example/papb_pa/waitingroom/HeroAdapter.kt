package com.example.papb_pa.waitingroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.papb_pa.R
import com.example.papb_pa.data.User
import kotlinx.android.synthetic.main.rowview.view.*
import kotlinx.android.synthetic.main.rowview3.view.*

class HeroAdapter(private val heroes: List<User>) : RecyclerView.Adapter<HeroHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): HeroHolder {
        return HeroHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.rowview3, viewGroup, false))
    }

    override fun getItemCount(): Int = heroes.size

    override fun onBindViewHolder(holder: HeroHolder, position: Int) {
        holder.bindHero(heroes[position])
    }
}

class HeroHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tvHeroName = view.jeneng_player_rank
    private val tvRank = view.rank

    fun bindHero(hero: User) {
        tvHeroName.text = hero.jeneng
        tvRank.text = adapterPosition.toString()
    }
}