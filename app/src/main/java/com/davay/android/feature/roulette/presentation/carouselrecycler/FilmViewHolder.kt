package com.davay.android.feature.roulette.presentation.carouselrecycler

import androidx.recyclerview.widget.RecyclerView
import com.davay.android.databinding.ItemSwipeableMovieCardBinding
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import com.davay.android.utils.MovieDetailsHelper
import com.davay.android.utils.MovieDetailsHelperImpl
import kotlin.math.roundToInt

class FilmViewHolder(private val binding: ItemSwipeableMovieCardBinding, parentWidth: Int) :
    RecyclerView.ViewHolder(binding.root) {

    private val movieDetailsHelper: MovieDetailsHelper = MovieDetailsHelperImpl()

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            (parentWidth * WIDTH_PERCENT).roundToInt(),
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    fun bind(film: MovieDetailsDemo) {
        binding.tvFilmTitle.text = film.movieName
        binding.tvOriginalTitle.text = film.englishName
        binding.tvYearCountryRuntime.text =
            movieDetailsHelper.buildStringYearCountriesRuntime(
                film.copy(countries = emptyList()),
                binding.root.context
            )

        movieDetailsHelper.setImage(binding.ivSelectMovieCover, film.posterUrl)
        movieDetailsHelper.setRateText(
            binding.tvMarkValue,
            film.ratingKinopoisk,
        )
    }

    companion object {
        private const val WIDTH_PERCENT = 0.75f
    }
}