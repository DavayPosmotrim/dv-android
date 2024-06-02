package com.davay.android.feature.roulette.presentation.carouselrecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davay.android.databinding.ItemFilmBinding
import com.davay.android.feature.roulette.presentation.model.FilmRouletteModel

class CarouselAdapter :
    RecyclerView.Adapter<FilmViewHolder>() {

    private val films: MutableList<FilmRouletteModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FilmViewHolder(ItemFilmBinding.inflate(inflater, parent, false), parent.width)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val actualPosition = position % films.size
        holder.bind(films[actualPosition])
    }

    override fun getItemCount(): Int = Integer.MAX_VALUE

    fun addFilms(list: List<FilmRouletteModel>) {
        films.clear()
        films.addAll(list)
        notifyDataSetChanged()
    }
}
