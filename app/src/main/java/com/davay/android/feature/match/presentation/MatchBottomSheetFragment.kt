package com.davay.android.feature.match.presentation

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.davay.android.databinding.FragmentMatchBottomSheetBinding
import com.davay.android.feature.selectmovie.MovieDetailsDemo
import com.davay.android.utils.MovieDetailsHelper
import com.davay.android.utils.MovieDetailsHelperImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class MatchBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMatchBottomSheetBinding? = null
    private val binding: FragmentMatchBottomSheetBinding
        get() = _binding!!

    private val movieDetailsHelper: MovieDetailsHelper = MovieDetailsHelperImpl()
    private var movieDetails: MovieDetailsDemo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieDetails =
                Gson().fromJson(it.getString(ARG_MOVIE_DETAILS), MovieDetailsDemo::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribe()
        movieDetails?.let { fillData(movieDetails!!) }
    }

    private fun initViews() {
        buildBottomSheet()
        hideUnusedItems()
    }

    private fun subscribe() {
        binding.matchProgressBtn.root.setOnClickListener {
            dismiss()
        }
    }

    private fun launchProgressButtonAnimation() {
        binding.matchProgressBtn.opvProgress.setProgressWithAnimation {
            dismiss()
        }
    }

    private fun buildBottomSheet() {
        dialog?.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            launchProgressButtonAnimation()
        }
    }

    private fun hideUnusedItems() {
        binding.matchMovieCard.apply {
            listOf(ivLike, ivSkip, ivRevert).forEach {
                it.visibility = View.GONE
            }
        }
    }

    private fun fillData(data: MovieDetailsDemo) {
        binding.matchMovieCard.apply {
            movieDetailsHelper.setImage(ivSelectMovieCover, data.posterUrl)
            movieDetailsHelper.addGenreList(fblGenreList, data.genres, requireContext())
            tvFilmTitle.text = data.movieName
            tvOriginalTitle.text = data.alternativeName ?: data.englishName ?: ""
            tvYearCountryRuntime.text =
                movieDetailsHelper.buildStringYearCountriesRuntime(data, requireContext())
            movieDetailsHelper.setRateText(tvMarkValue, data.ratingKinopoisk, requireContext())
        }
    }

    companion object {
        private const val ARG_MOVIE_DETAILS = "movie_details"

        fun newInstance(movieDetails: String): MatchBottomSheetFragment {
            val fragment = MatchBottomSheetFragment()
            val args = Bundle().apply {
                putString(ARG_MOVIE_DETAILS, movieDetails)
            }
            fragment.arguments = args
            return fragment
        }
    }
}