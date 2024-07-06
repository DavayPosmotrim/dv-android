package com.davay.android.feature.matchedsession.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.davai.extensions.dpToPx
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentMatchedSessionBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.coincidences.presentation.UiState
import com.davay.android.feature.coincidences.presentation.adapter.MoviesGridAdapter
import com.davay.android.feature.matchedsession.di.DaggerMatchedSessionFragmentComponent
import com.davay.android.feature.waitsession.presentation.adapter.CustomItemDecorator
import com.davay.android.feature.waitsession.presentation.adapter.UserAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MatchedSessionFragment :
    BaseFragment<FragmentMatchedSessionBinding, MatchedSessionViewModel>(FragmentMatchedSessionBinding::inflate) {

    override val viewModel: MatchedSessionViewModel by injectViewModel<MatchedSessionViewModel>()
    private val moviesGridAdapter = MoviesGridAdapter { movieId ->
        Toast.makeText(requireContext(), "Clicked!", Toast.LENGTH_SHORT).show()
    }
    private val userAdapter = UserAdapter()

    override fun diComponent(): ScreenComponent = DaggerMatchedSessionFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        initUsersRecycler()
        setupMoviesGrid()
        subscribe()
        userAdapter.setItems(
            listOf("Артем", "Руслан", "Константин", "Виктория")
        )

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

    private fun setupMoviesGrid() = with(binding.coincidencesList) {
        adapter = moviesGridAdapter
    }
//    Начало -> Взято из совпадений, чисто для демонстрации

    override fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {
                    handleState(it)
                }
            }
        }
    }

    private fun handleState(state: UiState) {
        when (state) {
            is UiState.Empty -> updateVisibility(emptyMessageIsVisible = true)
            is UiState.Loading -> updateVisibility(progressBarIsVisible = true)
            is UiState.Data -> {
                updateVisibility(coincidencesListIsVisible = true)
                moviesGridAdapter.setData(state.data)
            }

            is UiState.Error -> {
                Toast.makeText(requireContext(), "Error occurred!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateVisibility(
        progressBarIsVisible: Boolean = false,
        coincidencesListIsVisible: Boolean = false,
        emptyMessageIsVisible: Boolean = false
    ) = with(binding) {
        progressBar.isVisible = progressBarIsVisible
        coincidencesList.isVisible = coincidencesListIsVisible
        emptyPlaceholder.root.isVisible = emptyMessageIsVisible
    }

//        Конец -> Взято из совпадений, чисто для демонстрации

    private fun setupToolbar() {
        binding.toolbar.apply {
            addStatusBarSpacer()
            val sessionId = "VMst456"
            val subTitleText = "${R.string.session_list_name} $sessionId"
            setTitleText("23 сентября")
            setSubtitleText(subTitleText)
            setStartIconClickListener {
                viewModel.navigateBack()
            }
        }
    }

    companion object {
        private const val SPACING_BETWEEN_RV_ITEMS_8_DP = 8
    }
}