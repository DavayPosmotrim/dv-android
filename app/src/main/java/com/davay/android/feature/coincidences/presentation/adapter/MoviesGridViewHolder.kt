package com.davay.android.feature.coincidences.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davai.uikit.MovieCardView
import com.davay.android.feature.coincidences.presentation.TestMovie

class MoviesGridViewHolder(private val view: MovieCardView) : RecyclerView.ViewHolder(view) {

    fun bind(movie: TestMovie) = with(view) {
        setMovieCover(view.context.getString(movie.imageUrl))
        setMovieTitle(movie.title)
    }

    companion object {
        val create: (ViewGroup) -> MoviesGridViewHolder = { parent ->
            val view = MovieCardView(parent.context)
            MoviesGridViewHolder(view)
        }
    }
}