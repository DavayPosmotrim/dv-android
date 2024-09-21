package com.davay.android.feature.moviecard.presentation

import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.davai.extensions.dpToPx
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.databinding.FragmentSelectMovieBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.moviecard.di.DaggerMovieCardFragmentComponent
import com.davay.android.feature.selectmovie.presentation.AdditionalInfoInflater
import com.davay.android.utils.MovieDetailsHelper
import com.davay.android.utils.MovieDetailsHelperImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MovieCardFragment :
    BaseFragment<FragmentSelectMovieBinding, BaseViewModel>(FragmentSelectMovieBinding::inflate) {
    override val viewModel: MovieCardViewModel by injectViewModel<MovieCardViewModel>()

    override fun diComponent(): ScreenComponent = DaggerMovieCardFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    private val movieDetailsHelper: MovieDetailsHelper = MovieDetailsHelperImpl()
    private val additionalInfoInflater: AdditionalInfoInflater = MovieDetailsHelperImpl()

    private val movieDetails: MovieDetails by lazy {
        val args: MovieCardFragmentArgs by navArgs()
        args.movieDetails
    }

    override fun initViews() {
        setBottomSheet()
        hideButtonsOnCard()
        setToolbar()
        setViewsAndInflate()
    }

    private fun setToolbar() {
        binding.toolbarviewHeader.apply {
            hideEndIcon()
            setStartIcon(com.davai.uikit.R.drawable.ic_arrow_back)
            setStartIconClickListener {
                viewModel.navigateBack()
            }
            setTitleText(getString(R.string.movie_card_toolbar_title))
        }
    }

    private fun setViewsAndInflate() = with(binding) {
        rvFilmCard.isVisible = false
        movieCard.root.isVisible = true
        movieDetails.let { movie ->
            fillCardData(movie)
            fillInfo(movie)
            additionalInfoInflater.setRate(
                movie.ratingImdb,
                movie.numOfMarksImdb,
                mevDetailsImdbRate
            )
            additionalInfoInflater.setRate(
                movie.ratingKinopoisk,
                movie.numOfMarksKinopoisk,
                mevDetailsKinopoiskRate
            )
        }
    }

    private fun fillCardData(data: MovieDetails) {
        binding.movieCard.apply {
            movieDetailsHelper.setImage(ivSelectMovieCover, progressBar, data.imgUrl)
            movieDetailsHelper.addGenreList(fblGenreList, data.genres)
            tvFilmTitle.text = data.name
            tvOriginalTitle.text = data.alternativeName ?: ""
            tvYearCountryRuntime.text =
                movieDetailsHelper.buildStringYearCountriesRuntime(data, requireContext())
            movieDetailsHelper.setRateText(tvMarkValue, data.ratingKinopoisk)
        }
    }

    private fun fillInfo(data: MovieDetails) = with(binding) {
        tvDetailsDescription.text = data.description
        additionalInfoInflater.inflateCastList(
            fblDetailsTopCastList,
            data.actors ?: emptyList()
        )
        additionalInfoInflater.inflateCastList(
            fblDetailsDirectorList,
            data.directors ?: emptyList()
        )
    }

    private fun setBottomSheet() = with(binding) {
        BottomSheetBehavior.from(clDetailsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = BOTTOMSHEET_PEEK_HEIGHT_112_DP.dpToPx()

            clDetailsBottomSheet.post {
                val cardLocation = IntArray(2)
                movieCard.root.getLocationOnScreen(cardLocation)
                val cardTop = cardLocation[1]

                val screenHeight = activity?.resources?.displayMetrics?.heightPixels ?: 0
                val maxHeight = screenHeight - cardTop + MARGIN_TOP_16_DP.dpToPx()

                clDetailsBottomSheet.layoutParams.height = maxHeight
                clDetailsBottomSheet.requestLayout()
            }
        }
    }

    private fun hideButtonsOnCard() = with(binding.movieCard) {
        listOf(civLike, civSkip, civRevert).forEach {
            it.isVisible = false
        }
    }

    companion object {
        private const val BOTTOMSHEET_PEEK_HEIGHT_112_DP = 112
        private const val MARGIN_TOP_16_DP = 16
    }
}