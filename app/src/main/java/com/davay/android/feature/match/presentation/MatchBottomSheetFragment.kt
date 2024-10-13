package com.davay.android.feature.match.presentation

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.databinding.FragmentMatchBottomSheetBinding
import com.davay.android.feature.sessionsmatched.presentation.animation.AnimationMatchDialog
import com.davay.android.feature.sessionsmatched.presentation.animation.AnimationMatchDialogImpl
import com.davay.android.utils.MovieDetailsHelper
import com.davay.android.utils.MovieDetailsHelperImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MatchBottomSheetFragment(private val action: (() -> Unit)? = null) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentMatchBottomSheetBinding? = null
    private val binding: FragmentMatchBottomSheetBinding
        get() = _binding!!
    private val movieDetailsHelper: MovieDetailsHelper = MovieDetailsHelperImpl()
    private val animationMatchDialog: AnimationMatchDialog = AnimationMatchDialogImpl()
    private var movieDetails: MovieDetails? = null
    private var buttonText: String? = null
    private var showDismisAnimation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieDetails = Json.decodeFromString(it.getString(ARG_MOVIE_DETAILS) ?: "")
            buttonText = it.getString(ARG_BUTTON_TEXT)
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

            override fun onBackPressed() {
                action?.invoke()
                super.onBackPressed()
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
        buttonText?.let { setButtonText(it) }
    }

    private fun subscribe() {
        binding.progressButtonItem.root.setOnClickListener {
            animateDialogDismiss()
        }
    }

    private fun buildBottomSheet() {
        dialog?.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                peekHeight = Resources.getSystem().displayMetrics.heightPixels

                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            animationMatchDialog.animateBannerDrop(binding.tvBannerMatchWatch)
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            action?.invoke()
                            dismissAllowingStateLoss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Do nothing
                    }
                })
            }.also {
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            launchProgressButtonAnimation()
            animationMatchDialog.animateBannerDrop(binding.tvBannerMatchWatch)
        }
    }

    private fun launchProgressButtonAnimation() {
        binding.progressButtonItem.progressButton.also {
            lifecycleScope.launch {
                it.animateProgress(this) {
                    animateDialogDismiss()
                }
            }
        }
    }

    private fun hideUnusedItems() {
        binding.matchMovieCard.apply {
            listOf(civLike, civSkip, civRevert).forEach {
                it.visibility = View.GONE
            }
        }
    }

    private fun fillData(data: MovieDetails) {
        binding.matchMovieCard.apply {
            movieDetailsHelper.setImage(ivSelectMovieCover, progressBar, data.imgUrl)
            movieDetailsHelper.addGenreList(fblGenreList, data.genres)
            tvFilmTitle.text = data.name
            tvOriginalTitle.text = data.alternativeName ?: ""
            tvYearCountryRuntime.text =
                movieDetailsHelper.buildStringYearCountriesRuntime(data, requireContext())
            movieDetailsHelper.setRateText(tvMarkValue, data.ratingKinopoisk)
        }
    }

    private fun setButtonText(text: String) {
        binding.progressButtonItem.progressButton.text = text
    }

    private fun animateDialogDismiss() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
                ?: return
        if (showDismisAnimation == true) {
            animationMatchDialog.animateDialogDismiss(
                bottomSheet
            ) {
                dismissAllowingStateLoss()
                action?.invoke()
            }
        } else {
            dismissAllowingStateLoss()
        }
    }

    companion object {
        private const val ARG_MOVIE_DETAILS = "movie_details"
        private const val ARG_BUTTON_TEXT = "button_text"

        fun newInstance(
            movieDetails: String,
            buttonText: String? = null,
            showDismisAnimation: Boolean = true,
            action: (() -> Unit)? = null
        ): MatchBottomSheetFragment {
            val fragment = MatchBottomSheetFragment(action = action)
            fragment.showDismisAnimation = showDismisAnimation
            val args = Bundle().apply {
                putString(ARG_MOVIE_DETAILS, movieDetails)
                if (buttonText != null) putString(ARG_BUTTON_TEXT, buttonText)
            }
            fragment.arguments = args
            return fragment
        }
    }
}