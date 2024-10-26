package com.davay.android.feature.selectmovie.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.davai.extensions.dpToPx
import com.davai.uikit.dialog.MainDialogFragment
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.SessionStatus
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
import com.davay.android.feature.selectmovie.presentation.models.MovieMatchState
import com.davay.android.feature.selectmovie.presentation.models.SelectMovieState
import com.davay.android.utils.MovieDetailsHelperImpl
import com.davay.android.utils.presentation.UiErrorHandler
import com.davay.android.utils.presentation.UiErrorHandlerImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

@Suppress("LargeClass")
class SelectMovieFragment :
    BaseFragment<FragmentSelectMovieBinding, SelectMovieViewModel>(FragmentSelectMovieBinding::inflate) {

    override val viewModel: SelectMovieViewModel by injectViewModel<SelectMovieViewModel>()
    private val cardAdapter = MovieCardAdapter(
        coroutineScope = lifecycleScope,
        swipeLeft = { autoSwipeLeft() },
        swipeRight = { autoSwipeRight() },
        revert = { revertSwipe() },
        inflateMovieDetails = { movie -> inflateMovieDetails(movie) }
    )
    private val swipeCardLayoutManager = SwipeableLayoutManager()
    private val incrementAnimation: IncrementAnimation = IncrementAnimationImpl()
    private val additionalInfoInflater: AdditionalInfoInflater = MovieDetailsHelperImpl()
    private val errorHandler: UiErrorHandler = UiErrorHandlerImpl()
    private var currentPosition = swipeCardLayoutManager.getCurrentPosition()

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
            currentPosition++
            swipeCardLayoutManager.updateCurrentPosition(currentPosition)
        }
    }

    override fun initViews() {
        buildRecyclerView()
        setBottomSheet()
    }

    override fun subscribe() {
        backPressedDispatcher()
        binding.toolbarviewHeader.apply {
            setEndIconClickListener {
                viewModel.navigate(SelectMovieFragmentDirections.actionSelectMovieFragmentToCoincidencesFragment())
            }
            setStartIconClickListener {
                showleaveSessionDialog()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                renderState(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.matchState.collect { state ->
                handleMovieMatchState(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sessionStatusState.collect { state ->
                when (state) {
                    SessionStatus.CLOSED -> {
                        showConfirmDialogAtSessionClosedStatus()
                    }

                    SessionStatus.ROULETTE -> showConfirmDialogAndNavigateToRoulette()
                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stateMatchesCounter.collect { counter ->
                binding.toolbarviewHeader.updateMatchesDisplay(counter)
            }
        }
    }

    private fun renderState(state: SelectMovieState) {
        when (state) {
            is SelectMovieState.Loading -> showProgressBar()
            is SelectMovieState.Content -> showContent(state)
            is SelectMovieState.Error -> handleError(state)
            is SelectMovieState.ListIsFinished -> showDialogAndRequestResetMovieList()
        }
    }

    private fun handleMovieMatchState(state: MovieMatchState) {
        when (state) {
            is MovieMatchState.Empty -> {}
            is MovieMatchState.Content -> {
                showBottomSheetFragment(state.movieDetails)
                viewModel.emptyMovieMatchState()
            }
        }
    }

    private fun handleError(state: SelectMovieState.Error) {
        showErrorMessage()
        errorHandler.handleError(
            state.errorType,
            binding.errorMessage
        ) {
            viewModel.onMovieSwiped(currentPosition, false)
        }
    }

    private fun showErrorMessage() = with(binding) {
        errorMessage.isVisible = true
        progressBar.isVisible = false
        rvFilmCard.isVisible = false
        clDetailsBottomSheet.isVisible = false
    }

    private fun showContent(state: SelectMovieState.Content) = with(binding) {
        cardAdapter.setData(state.movieList)
        errorMessage.isVisible = false
        progressBar.isVisible = false
        rvFilmCard.isVisible = true
        clDetailsBottomSheet.isVisible = true
    }

    private fun showProgressBar() = with(binding) {
        errorMessage.isVisible = false
        progressBar.isVisible = true
        rvFilmCard.isVisible = false
        clDetailsBottomSheet.isVisible = false
    }

    private fun showDialogAndRequestResetMovieList() {
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.select_movies_movie_list_is_finished),
            message = getString(R.string.select_movies_movie_show_disliked_movies),
            showConfirmBlock = true,
            yesAction = {
                confirmAction()
            },
            onCancelAction = {
                confirmAction()
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun confirmAction() {
        cardAdapter.setData(emptySet())
        swipeCardLayoutManager.updateCurrentPosition(0)
        viewModel.filterDislikedMovieList()
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
                viewModel.leaveSessionAndNavigateToHistory()
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun showleaveSessionDialog() {
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.leave_session_title),
            message = getString(R.string.select_movies_leave_session_dialog_message),
            yesAction = {
                viewModel.getSessionDataSaveToDbAndLeaveSession()
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun showConfirmDialogAtSessionClosedStatus() {
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.select_movies_session_is_closed),
            message = getString(R.string.leave_session_dialog_message_session_complited),
            showConfirmBlock = true,
            yesAction = {
                viewModel.getSessionDataSaveToDbAndLeaveSession()
            },
            onCancelAction = {
                viewModel.getSessionDataSaveToDbAndLeaveSession()
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun showConfirmDialogAndNavigateToRoulette() {
        val action =
            SelectMovieFragmentDirections.actionSelectMovieFragmentToRouletteFragment(false)
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.select_movies_roulette_is_running_title),
            message = getString(R.string.select_movies_roulette_is_running_message),
            showConfirmBlock = true,
            yesAction = {
                viewModel.navigate(action)
            },
            onCancelAction = {
                viewModel.navigate(action)
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun buildRecyclerView() {
        binding.rvFilmCard.apply {
            layoutManager = swipeCardLayoutManager
            adapter = cardAdapter
            // помимо установки позици на layputmanger дополнительно скролим до необходимой позиции
            scrollToPosition(currentPosition)
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeCallback(
                swipeCardLayoutManager,
                onSwipedLeft = {
                    swipeLeft()
                },
                onSwipedRight = {
                    swipeRight()
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

    private fun swipeLeft() {
        swipe()
        swipeCardLayoutManager.moveNextWithSwipeAndLayout(SwipeDirection.LEFT)
        viewModel.onMovieSwiped(currentPosition, false)
    }

    private fun swipeRight() {
        swipe()
        swipeCardLayoutManager.moveNextWithSwipeAndLayout(SwipeDirection.RIGHT)
        viewModel.onMovieSwiped(currentPosition, true)
    }

    /**
     * методы autoSwipeLeft и autoSwipeLeft используются для автоматического свайпа для cardAdapter
     * и полностью дублируют swipeLeft и swipeRight за исключением обновления currentPosition,
     * currentPosition при кликах на кнопки в адаптере отличается от currentPosition при свайпе на 1
     */
    private fun autoSwipeLeft() {
        swipe()
        swipeCardLayoutManager.moveNextWithSwipeAndLayout(SwipeDirection.LEFT)
        currentPosition++ // обновление позиции после свайпа для синхронизации позиции со значением в БД
        viewModel.onMovieSwiped(currentPosition, false)
    }

    private fun autoSwipeRight() {
        swipe()
        swipeCardLayoutManager.moveNextWithSwipeAndLayout(SwipeDirection.RIGHT)
        currentPosition++ // обновление позиции после свайпа для синхронизации позиции со значением в БД
        viewModel.onMovieSwiped(currentPosition, true)
    }

    private fun revertSwipe() {
        swipe()
        swipeCardLayoutManager.shiftLeftWithRevertAndLayout()
        viewModel.onMovieSwiped(currentPosition, false)
    }

    private fun swipe() {
        currentPosition = swipeCardLayoutManager.getCurrentPosition()
        cardAdapter.notifyDataSetChanged()
    }

    private fun inflateMovieDetails(movie: MovieDetails) = with(binding) {
        tvDetailsDescription.text = movie.description
        fillInfo(movie)
        if (movie.ratingImdb >= 1f) {
            mevDetailsImdbRate.isVisible = true
            additionalInfoInflater.setRate(
                movie.ratingImdb,
                movie.numOfMarksImdb,
                mevDetailsImdbRate
            )
        } else {
            mevDetailsImdbRate.isVisible = false
        }
        if (movie.ratingKinopoisk >= 1f) {
            mevDetailsKinopoiskRate.isVisible = true
            additionalInfoInflater.setRate(
                movie.ratingKinopoisk,
                movie.numOfMarksKinopoisk,
                mevDetailsKinopoiskRate
            )
        } else {
            mevDetailsKinopoiskRate.isVisible = false
        }
    }

    private fun fillInfo(data: MovieDetails) = with(binding) {
        tvDetailsDescription.text = data.description
        if (data.actors.isNullOrEmpty()) {
            tvDetailsTopCast.isVisible = false
            fblDetailsTopCastList.isVisible = false
        } else {
            tvDetailsTopCast.isVisible = true
            fblDetailsTopCastList.isVisible = true
            additionalInfoInflater.inflateCastList(
                fblDetailsTopCastList,
                data.actors
            )
        }
        if (data.directors.isNullOrEmpty()) {
            tvDetailsDirector.isVisible = false
            fblDetailsDirectorList.isVisible = false
        } else {
            tvDetailsDirector.isVisible = true
            fblDetailsDirectorList.isVisible = true
            additionalInfoInflater.inflateCastList(
                fblDetailsDirectorList,
                data.directors
            )
        }
    }

    /**
     * Использовать метод в событиях мэтча в вэбсокете
     * В action передаем изменение стэйта MovieMatchState в Empty
     */
    private fun showBottomSheetFragment(movie: MovieDetails) {
        val bottomSheetFragment = MatchBottomSheetFragment.newInstance(
            movieDetails = movie,
            action = {
                incrementAnimation.animate(binding.tvMotionedIncrement) {
                    viewModel.getMatchesCount()
                }
            }
        )

        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private companion object {
        const val BOTTOMSHEET_PEEK_HEIGHT_112_DP = 112
        const val MARGIN_TOP_16_DP = 16
        const val CURRENT_POSITION_KEY = "currentPosition"
        val TAG = SelectMovieFragment::class.simpleName
    }
}
