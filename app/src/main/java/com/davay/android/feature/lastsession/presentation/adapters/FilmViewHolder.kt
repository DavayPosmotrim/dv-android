package com.davay.android.feature.lastsession.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davai.uikit.MovieCardView


class FilmViewHolder(
    private val film: MovieCardView
) : RecyclerView.ViewHolder(film) {
    fun bind(filmName: String) {
        film.setMovieTitle(filmName)
        film.setMovieCover("")
    }

    companion object {
        fun from(parent: ViewGroup): FilmViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(com.davai.uikit.R.layout.movie_item, parent, false) as MovieCardView
            return FilmViewHolder(view)
        }
    }
}
