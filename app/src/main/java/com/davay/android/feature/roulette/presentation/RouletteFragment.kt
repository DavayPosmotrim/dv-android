package com.davay.android.feature.roulette.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.davai.uikit.TagView
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentRouletteBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.roulette.di.DaggerRouletteFragmentComponent
import com.davay.android.feature.roulette.presentation.carouselrecycler.CarouselAdapter
import com.davay.android.feature.roulette.presentation.carouselrecycler.CarouselLayoutManager
import com.davay.android.feature.roulette.presentation.carouselrecycler.LinearHorizontalSpacingDecoration
import com.davay.android.feature.roulette.presentation.model.FilmRouletteModel
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

class RouletteFragment :
    BaseFragment<FragmentRouletteBinding, RouletteViewModel>(FragmentRouletteBinding::inflate) {

    override val viewModel: RouletteViewModel by injectViewModel<RouletteViewModel>()
    private val carouselAdapter: CarouselAdapter = CarouselAdapter()
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(binding.bottomSheetRoulette) }

    override fun diComponent(): ScreenComponent =
        DaggerRouletteFragmentComponent.builder().appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.state.collect {
                handleState(it)
            }
        }
    }

    private fun initRecyclerRoulette(films: List<FilmRouletteModel>) {
        carouselAdapter.addFilms(films)
        with(binding.recyclerViewRoulette) {
            layoutManager = CarouselLayoutManager(requireContext())
            adapter = carouselAdapter
            val spacing = resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_16)
            addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
            LinearSnapHelper().attachToRecyclerView(this)
        }
        startAutoScrolling()
    }

    private fun startAutoScrolling() {
        with(binding.recyclerViewRoulette) {
            (layoutManager as CarouselLayoutManager).speed = CarouselLayoutManager.SPEED_LOW
            post {
                smoothScrollToPosition(Int.MAX_VALUE)
            }
        }
    }

    private fun startRouletteScrolling(position: Int) {
        with(binding.recyclerViewRoulette) {
            (layoutManager as CarouselLayoutManager).speed = CarouselLayoutManager.SPEED_HIGH
            post {
                smoothScrollToPosition(position)
            }
            binding.recyclerViewRoulette.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.rouletteScrollingStopped()
                    }
                }
            })
        }
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initBottomSheet(participantsList: List<UserRouletteModel>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.fblParticipants.apply {
            setDividerDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    com.davai.uikit.R.drawable.divider_8,
                    requireActivity().theme
                )
            )
            setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE)
        }
        participantsList.forEach { user ->
            val participantsView = LayoutInflater.from(requireContext()).inflate(
                R.layout.item_participants,
                binding.fblParticipants,
                false
            ) as TagView
            participantsView.setText(user.name)
            binding.fblParticipants.addView(participantsView)
        }
    }

    private fun handleState(state: RouletteState) {
        when (state) {
            RouletteState.Error -> handleErrorState()
            is RouletteState.Match -> handleMatchState(state)
            is RouletteState.Roulette -> handleRouletteState(state)
            is RouletteState.Waiting -> handleWaitingState(state)
            is RouletteState.Init -> handleInitState(state)
        }
    }

    private fun handleErrorState() {
        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
    }

    private fun handleMatchState(state: RouletteState.Match) {
        with(binding.includedFilm) {
            tvFilmTitle.text = state.film.title
            tvOriginalTitle.text = state.film.originalTitle
            tvMarkValue.text = state.film.mark
            tvYearCountryRuntime.text = state.film.yearCountryRuntime
            ivSelectMovieCover.load(state.film.posterUrl) {
                placeholder(com.davai.uikit.R.drawable.placeholder_general_80)
                    .scale(Scale.FIT)
                error(com.davai.uikit.R.drawable.placeholder_general_80)
                    .scale(Scale.FIT)
                transformations(RoundedCornersTransformation())
                    .crossfade(true)
            }
        }
    }

    private fun handleRouletteState(state: RouletteState.Roulette) {
        binding.recyclerViewRoulette.stopScroll()
        hideBottomSheet()
        val currentPosition =
            (binding.recyclerViewRoulette.layoutManager as CarouselLayoutManager)
                .findLastVisibleItemPosition()
        val position =
            currentPosition + currentPosition % state.count + state.index + state.count * 4
        startRouletteScrolling(position)
    }

    private fun handleWaitingState(state: RouletteState.Waiting) {
        state.users.forEachIndexed { index, user ->
            if (user.isConnected) {
                // Важен порядок
                (binding.fblParticipants[index] as TagView).changeStyle(TagView.Companion.Style.SECONDARY_GREEN)
            }
        }
    }

    private fun handleInitState(state: RouletteState.Init) {
        initBottomSheet(state.users)
        initRecyclerRoulette(state.films)
    }
}
