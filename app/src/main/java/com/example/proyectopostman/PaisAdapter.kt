package com.example.proyectopostman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopostman.PaisCard
import com.example.proyectopostman.R
import com.bumptech.glide.Glide

class PaisAdapter (private var cards: List<PaisCard>) : RecyclerView.Adapter<PaisAdapter.ViewHolder>(){



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(card: PaisCard) {
            itemView.setOnClickListener {

                println("item pulsado")

            }
            itemView.findViewById<TextView>(R.id.tvRareza).text = card.capital
            itemView.findViewById<TextView>(R.id.tvTitle).text = card.name
            /*
            Glide.with(itemView.context)
                .load(card.imageUrl)
                .into(itemView.findViewById<ImageView>(R.id.ivMain))

             */
        }


    }

    fun setList(lista2: MutableList<PaisCard>) {
        this.cards = lista2
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_manus, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount() = cards.size
}


