package com.davay.android.feature.matchedsession.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.davai.extensions.dpToPx
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.databinding.FragmentMatchedSessionBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.extensions.formatDateWithoutCurrentYear
import com.davay.android.extensions.timeStamp
import com.davay.android.feature.coincidences.presentation.adapter.MoviesGridAdapter
import com.davay.android.feature.matchedsession.di.DaggerMatchedSessionFragmentComponent
import com.davay.android.feature.matchedsession.presentation.adapter.CustomItemDecorator
import com.davay.android.feature.matchedsession.presentation.adapter.UserAdapter
import com.davay.android.feature.moviecard.presentation.MovieCardFragment
import com.davay.android.utils.presentation.UiErrorHandler
import com.davay.android.utils.presentation.UiErrorHandlerImpl
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MatchedSessionFragment :
    BaseFragment<FragmentMatchedSessionBinding, MatchedSessionViewModel>(
        FragmentMatchedSessionBinding::inflate
    ) {

    override val viewModel: MatchedSessionViewModel by injectViewModel<MatchedSessionViewModel>()
    private val moviesGridAdapter = MoviesGridAdapter { movieDetails ->
        val movie = Json.encodeToString(movieDetails)
        val bundle = Bundle().apply {
            putString(MovieCardFragment.MOVIE_DETAILS_KEY, movie)
        }
        viewModel.navigate(R.id.action_matchedSessionFragment_to_movieCardFragment, bundle)
    }
    private val userAdapter = UserAdapter()
    private var sessionId = ""
    private val errorHandler: UiErrorHandler = UiErrorHandlerImpl()

    override fun diComponent(): ScreenComponent = DaggerMatchedSessionFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            sessionId = it.getString(SESSION_ID, "")
        }
        super.onViewCreated(view, savedInstanceState)

        initUsersRecycler()
        setupMoviesGrid()
    }

    private fun initUsersRecycler() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }
        val spaceBetweenItems = SPACING_BETWEEN_RV_ITEMS_8_DP.dpToPx()

        binding.rvUser.apply {
            adapter = userAdapter
            layoutManager = flexboxLayoutManager
            addItemDecoration(CustomItemDecorator(spaceBetweenItems))
        }
    }

    private fun setupMoviesGrid() {
        binding.coincidencesList.adapter = moviesGridAdapter
    }

    override fun subscribe() {
        viewModel.getSessionData(sessionId)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {
                    handleState(it)
                }
            }
        }
    }

    private fun handleState(state: MatchedSessionState) {
        when (state) {
            is MatchedSessionState.Empty -> {
                val date = state.data.session.date
                val subTitle =
                    resources.getString(R.string.matched_session_session, state.data.session.id)
                setupToolbar(subTitle, date)
                userAdapter.setItems(state.data.session.users)
                updateVisibility(coincidencesListIsVisible = false, errorMessageVisible = true)
                errorHandler.handleError(
                    ErrorScreenState.EMPTY,
                    binding.errorMessage,
                    null
                )
            }

            is MatchedSessionState.Loading -> {
                setupToolbar()
                updateVisibility(progressBarIsVisible = true)
            }

            is MatchedSessionState.Data -> {
                val date = state.data.session.date
                val subTitle =
                    resources.getString(R.string.matched_session_session, state.data.session.id)
                setupToolbar(subTitle, date)
                userAdapter.setItems(state.data.session.users)
                updateVisibility(coincidencesListIsVisible = true)
                moviesGridAdapter.setData(state.data.movies)
            }

            is MatchedSessionState.Error -> {
                setupToolbar()
                updateVisibility(coincidencesListIsVisible = false, errorMessageVisible = true)
                errorHandler.handleError(
                    state.errorType,
                    binding.errorMessage,
                    null
                )
            }
        }
    }

    private fun updateVisibility(
        progressBarIsVisible: Boolean = false,
        coincidencesListIsVisible: Boolean = false,
        errorMessageVisible: Boolean = false
    ) = with(binding) {
        progressBar.isVisible = progressBarIsVisible
        coincidencesList.isVisible = coincidencesListIsVisible
        errorMessage.isVisible = errorMessageVisible
    }

    private fun setupToolbar(
        subTitle: String = resources.getString(com.davai.uikit.R.string.empty_text),
        date: timeStamp = System.currentTimeMillis(),
    ) {
        binding.toolbar.apply {
            setTitleText(date.formatDateWithoutCurrentYear())
            setSubtitleText(subTitle)
            setStartIconClickListener {
                viewModel.navigateBack()
            }
        }
    }

    companion object {
        private const val SPACING_BETWEEN_RV_ITEMS_8_DP = 8
        const val SESSION_ID = "session_id"
    }
}