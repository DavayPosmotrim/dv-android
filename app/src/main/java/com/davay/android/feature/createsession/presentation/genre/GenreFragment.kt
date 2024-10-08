package com.davay.android.feature.createsession.presentation.genre

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.davai.uikit.BannerView
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.presentation.MainActivity
import com.davay.android.databinding.FragmentGenreBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.createsession.di.DaggerCreateSessionFragmentComponent
import com.davay.android.feature.createsession.presentation.genre.adapter.GenreAdapter
import com.davay.android.utils.presentation.UiErrorHandler
import com.davay.android.utils.presentation.UiErrorHandlerImpl
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch

class GenreFragment : BaseFragment<FragmentGenreBinding, GenreViewModel>(
    FragmentGenreBinding::inflate
) {
    override val viewModel: GenreViewModel by injectViewModel<GenreViewModel>()
    private var genreAdapter = GenreAdapter { genre ->
        viewModel.genreClicked(genre)
    }

    private val errorHandler: UiErrorHandler = UiErrorHandlerImpl()

    override fun diComponent(): ScreenComponent = DaggerCreateSessionFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun initViews() {
        super.initViews()
        initRecycler()
    }

    override fun subscribe() {
        super.subscribe()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.genreState.collect { state ->
                renderState(state)
            }
        }
    }

    private fun initRecycler() {
        binding.rvGenre.adapter = genreAdapter
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }
        binding.rvGenre.layoutManager = layoutManager
    }

    private fun renderState(state: GenreState) {
        when (state) {
            is GenreState.Loading -> showProgressBar()
            is GenreState.Content -> handleContent(state)
            is GenreState.Error -> handleError(state)
            is GenreState.CreateSessionLoading -> showProgressBar()
        }
    }

    private fun handleError(state: GenreState.Error) {
        showErrorMessage()
        errorHandler.handleError(
            state.errorType,
            binding.errorMessage
        ) {
            viewModel.getGenreList()
        }
    }

    private fun showErrorMessage() = with(binding) {
        errorMessage.isVisible = true
        progressBar.isVisible = false
        rvGenre.isVisible = false
    }

    private fun handleContent(state: GenreState.Content) {
        genreAdapter.updateItemList(state.genreList)
        showContent()
    }

    private fun showContent() = with(binding) {
        errorMessage.isVisible = false
        progressBar.isVisible = false
        rvGenre.isVisible = true
    }

    private fun showProgressBar() = with(binding) {
        errorMessage.isVisible = false
        progressBar.isVisible = true
        rvGenre.isVisible = true
    }

    /**
     * Метод вызывается на CreateSessionFragment по клику на кнопку "Продолжить"
     */
    fun buttonContinueClicked() {
        updateBanner()
        viewModel.createSessionAndNavigateToWaitSessionScreen {
            (requireActivity() as MainActivity).showBanner()
        }
    }

    private fun updateBanner() {
        (requireActivity() as MainActivity).updateBanner(
            getString(R.string.create_session_choose_genre_one),
            BannerView.ATTENTION
        )
    }

    /**
     * Сбрасывает список выбранных жанров
     * Используется при обновлении статуса при создании сессии.
     */
    fun resetSelections() {
        viewModel.resetSelections()
        genreAdapter.clearSelections()
    }

    companion object {
        fun newInstance() = GenreFragment()
    }
}
