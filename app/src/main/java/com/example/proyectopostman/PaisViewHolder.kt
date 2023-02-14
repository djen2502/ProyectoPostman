package com.example.proyectopostman

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopostman.databinding.RowManusBinding

class PaisViewHolder (vista: View) : RecyclerView.ViewHolder(vista) {
    //  private val miBinding=UsuariosLayoutBinding.bind(vista)
    private val miBinding = RowManusBinding.bind(vista)
    fun inflar(
        anime: Pais
    ) {
        if (anime.nombre.length < 25) {

            miBinding.tvTitle.text = anime.nombre
        }else{
            miBinding.tvTitle.text = anime.nombre.take(25)+" ..."

        }


    }


}