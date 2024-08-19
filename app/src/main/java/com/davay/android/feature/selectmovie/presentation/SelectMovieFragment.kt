package com.davay.android.feature.selectmovie.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.davai.extensions.dpToPx
import com.davai.uikit.MainDialogFragment
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.databinding.FragmentSelectMovieBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.extensions.SwipeDirection
import com.davay.android.feature.match.presentation.MatchBottomSheetFragment
import com.davay.android.feature.selectmovie.di.DaggerSelectMovieFragmentComponent
import com.davay.android.feature.selectmovie.presentation.adapters.MovieCardAdapter
import com.davay.android.feature.selectmovie.presentation.adapters.SwipeCallback
import com.davay.android.feature.selectmovie.presentation.adapters.SwipeableLayoutManager
import com.davay.android.feature.selectmovie.presentation.animation.IncrementAnimation
import com.davay.android.feature.selectmovie.presentation.animation.IncrementAnimationImpl
import com.davay.android.utils.MovieDetailsHelperImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SelectMovieFragment :
    BaseFragment<FragmentSelectMovieBinding, SelectMovieViewModel>(FragmentSelectMovieBinding::inflate) {

    override val viewModel: SelectMovieViewModel by injectViewModel<SelectMovieViewModel>()
    private var matchesCounter = 0
    private val cardAdapter = MovieCardAdapter(
        swipeLeft = { autoSwipeLeft() },
        swipeRight = { autoSwipeRight() },
        revert = { revertSwipe() },
        inflateMovieDetails = { movie -> inflateMovieDetails(movie) }
    )
    private val swipeCardLayoutManager = SwipeableLayoutManager()
    private val incrementAnimation: IncrementAnimation = IncrementAnimationImpl()
    private val additionalInfoInflater: AdditionalInfoInflater = MovieDetailsHelperImpl()
    private var currentPosition = 0

    override fun diComponent(): ScreenComponent =
        DaggerSelectMovieFragmentComponent.builder().appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSavedPositionAndUpdateStartPosition(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentPosition = swipeCardLayoutManager.getCurrentPosition()
        outState.putInt(CURRENT_POSITION_KEY, currentPosition)
    }

    override fun onDestroyView() {
        binding.rvFilmCard.layoutManager = null
        super.onDestroyView()
    }

    private fun getSavedPositionAndUpdateStartPosition(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            currentPosition = it.getInt(CURRENT_POSITION_KEY, 0)
        }
        swipeCardLayoutManager.updateCurrentPosition(currentPosition)
    }

    override fun initViews() {
        buildRecyclerView()
        setToolbar()
        setBottomSheet()
    }

    override fun subscribe() {
        backPressedDispatcher()
        binding.toolbarviewHeader.apply {
            setEndIconClickListener {
                viewModel.navigate(R.id.action_selectMovieFragment_to_coincidencesFragment)
            }
            setStartIconClickListener {
                showDialogAndNavigateToHistorySessions()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { movies ->
                cardAdapter.setData(movies)
            }
        }
    }

    private fun backPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showDialogAndNavigateToHistorySessions()
                }
            }
        )
    }

    private fun showDialogAndNavigateToHistorySessions() {
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.leave_session_title),
            message = getString(R.string.select_movies_leave_session_dialog_message),
            yesAction = {
                viewModel.clearBackStackToMainAndNavigate(R.id.action_mainFragment_to_matchedSessionListFragment)
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    /**
     * Метод вызывается у юзеров, у которых из сессии вышел участник
     * !добавить сохранение сессии в БД тут и в showDialogAndNavigateToHistorySessions()
     */
    @Suppress("Detekt.UnusedPrivateMember")
    private fun showConfirmDialogAndNavigateToHistorySessions() {
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.leave_session_title),
            message = getString(R.string.leave_session_dialog_message_session_complited),
            showConfirmBlock = true,
            yesAction = {
                viewModel.clearBackStackToMainAndNavigate(R.id.action_mainFragment_to_matchedSessionListFragment)
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun buildRecyclerView() {
        binding.rvFilmCard.apply {
            layoutManager = swipeCardLayoutManager
            adapter = cardAdapter
            // помимо установки позици на layputmanger дополни тельно скролим до необходимой позиции
            scrollToPosition(currentPosition)
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeCallback(
                swipeCardLayoutManager,
                onSwipedLeft = {
                    // Add skip method
                },
                onSwipedRight = {
                    // Add like method
                }
            )
        )

        itemTouchHelper.attachToRecyclerView(binding.rvFilmCard)
    }

    private fun setBottomSheet() = with(binding) {
        BottomSheetBehavior.from(clDetailsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = BOTTOMSHEET_PEEK_HEIGHT_112_DP.dpToPx()

            clDetailsBottomSheet.post {
                val cardLocation = IntArray(2)
                rvFilmCard.getLocationOnScreen(cardLocation)
                val cardTop = cardLocation[1]

                val screenHeight = activity?.resources?.displayMetrics?.heightPixels ?: 0
                val maxHeight = screenHeight - cardTop + MARGIN_TOP_16_DP.dpToPx()

                clDetailsBottomSheet.layoutParams.height = maxHeight
                clDetailsBottomSheet.requestLayout()
            }
        }
    }

    private fun setToolbar() {
        binding.toolbarviewHeader.apply {
            updateMatchesDisplay(matchesCounter)
        }
    }

    private fun autoSwipeLeft() {
        swipeCardLayoutManager.moveNextWithSwipeAndLayout(SwipeDirection.LEFT)
        cardAdapter.notifyDataSetChanged()
    }

    private fun autoSwipeRight() {
        swipeCardLayoutManager.moveNextWithSwipeAndLayout(SwipeDirection.RIGHT)
        cardAdapter.notifyDataSetChanged()
    }

    private fun revertSwipe() {
        swipeCardLayoutManager.shiftLeftWithRevertAndLayout()
        cardAdapter.notifyDataSetChanged()
    }

    private fun inflateMovieDetails(movie: MovieDetails) = with(binding) {
        tvDetailsDescription.text = movie.description
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

    @Suppress("Detekt.UnusedPrivateMember")
    private fun showBottomSheetFragment(movie: MovieDetails) {
        val movieDetails = Json.encodeToString(movie)
        val bottomSheetFragment = MatchBottomSheetFragment.newInstance(
            movieDetails,
            action = {
                incrementAnimation.animate(binding.tvMotionedIncrement) {
                    binding.toolbarviewHeader.incrementMatchesDisplay()
                }
            }
        )
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private companion object {
        const val BOTTOMSHEET_PEEK_HEIGHT_112_DP = 112
        const val MARGIN_TOP_16_DP = 16
        const val CURRENT_POSITION_KEY = "currentPosition"
    }
}

