package com.davay.android.feature.coincidences.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davai.uikit.MovieCardView
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo

class MoviesGridViewHolder(private val view: MovieCardView) : RecyclerView.ViewHolder(view) {

    fun bind(movie: MovieDetailsDemo) = with(view) {
        setMovieCover(movie.posterUrl ?: "")
        setMovieTitle(movie.name)
    }

    companion object {
        val create: (ViewGroup) -> MoviesGridViewHolder = { parent ->
            val view = MovieCardView(parent.context)
            MoviesGridViewHolder(view)
        }
    }
}