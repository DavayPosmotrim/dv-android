package com.davay.android.feature.roulette.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentRouletteBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.match.presentation.MatchBottomSheetFragment
import com.davay.android.feature.roulette.di.DaggerRouletteFragmentComponent
import com.davay.android.feature.roulette.presentation.carouselrecycler.CarouselAdapter
import com.davay.android.feature.roulette.presentation.carouselrecycler.CarouselLayoutManager
import com.davay.android.feature.roulette.presentation.carouselrecycler.LinearHorizontalSpacingDecoration
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel
import com.davay.android.feature.roulette.presentation.useradapter.UserAdapter
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import com.davay.android.feature.waitsession.presentation.adapter.CustomItemDecorator
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RouletteFragment :
    BaseFragment<FragmentRouletteBinding, RouletteViewModel>(FragmentRouletteBinding::inflate) {

    override val viewModel by injectViewModel<RouletteViewModel>()
    private val bottomSheetBehaviorWaiting by lazy { BottomSheetBehavior.from(binding.bottomSheetWaiting) }
    private val bottomSheetBehaviorIntro by lazy { BottomSheetBehavior.from(binding.bottomSheetIntro) }
    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            super.onFragmentDetached(fm, f)
            if (f is MatchBottomSheetFragment) {
                findNavController().navigateUp()
            }
        }
    }

    override fun diComponent(): ScreenComponent =
        DaggerRouletteFragmentComponent.builder().appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("MyTag", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        lifecycleScope.launch {
            viewModel.state.collect {
                handleState(it)
            }
        }
        if (viewModel.state.value !is RouletteState.Match && viewModel.state.value !is RouletteState.Error) {
            handleStartFragment()
        } else {
            bottomSheetBehaviorIntro.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MyTag", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MyTag", "onResume")
        if (viewModel.state.value is RouletteState.Match) {
            handleMatchState(viewModel.state.value as RouletteState.Match)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MyTag", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MyTag", "onStop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("MyTag", "outState: $outState")
    }

    override fun onDestroyView() {
        Log.d("MyTag", "onDestroyView")
        binding.recyclerViewRoulette.clearOnScrollListeners()
        super.onDestroyView()
        parentFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

    /**
     *  Для инициатора рулетки в bundle должно быть true по ключу ROULETTE_INITIATOR.
     *  Для остальных пусто или false.
     */
    private fun handleStartFragment() {
        Log.d("MyTag", "handleStartFragment")
        val isInitiator: Boolean? = arguments?.getBoolean(ROULETTE_INITIATOR)
        if (isInitiator == true) {
            arguments?.remove(ROULETTE_INITIATOR)
            initBottomSheetIntro()
        } else {
            bottomSheetBehaviorIntro.state = BottomSheetBehavior.STATE_HIDDEN
            startAutoScrolling()
        }
    }

    private fun initBottomSheetIntro() {
        Log.d("MyTag", "initBottomSheetIntro")
        bottomSheetBehaviorIntro.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehaviorIntro.isHideable = false
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnContinue.setOnClickListener {
            bottomSheetBehaviorIntro.isHideable = true
            bottomSheetBehaviorIntro.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.rouletteStart()
            startAutoScrolling()
        }
    }

    private fun initRecyclerRoulette(films: List<MovieDetailsDemo>) {
        Log.d("MyTag", "initRecyclerRoulette")
        val carouselAdapter = CarouselAdapter().apply {
            addFilms(films)
        }
        with(binding.recyclerViewRoulette) {
            layoutManager = CarouselLayoutManager(requireContext())
            adapter = carouselAdapter
            val spacing =
                resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_negative_16)
            addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
            LinearSnapHelper().attachToRecyclerView(this)
        }
    }

    private fun startAutoScrolling() {
        Log.d("MyTag", "startAutoScrolling")
        with(binding.recyclerViewRoulette) {
            (layoutManager as CarouselLayoutManager).setSlowSpeedTransition()
            post {
                smoothScrollToPosition(Int.MAX_VALUE)
            }
        }
    }

    private fun startRouletteScrolling(position: Int) {
        Log.d("MyTag", "startRouletteScrolling")
        with(binding.recyclerViewRoulette) {
            (layoutManager as CarouselLayoutManager).setFastSpeedTransition()
            post {
                smoothScrollToPosition(position)
            }
            binding.recyclerViewRoulette.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && activity?.isDestroyed == false) {
                        viewModel.rouletteScrollingStopped()
                    }
                }
            })
        }
    }

    private fun hideBottomSheetWaiting() {
        Log.d("MyTag", "hideBottomSheetWaiting")
        bottomSheetBehaviorWaiting.isHideable = true
        bottomSheetBehaviorWaiting.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initBottomSheetWaiting(participantsList: List<UserRouletteModel>) {
        Log.d("MyTag", "initBottomSheetWaiting")
        bottomSheetBehaviorWaiting.state = BottomSheetBehavior.STATE_EXPANDED
        binding.rvParticipants.adapter = UserAdapter().apply {
            setItems(participantsList)
        }
        binding.rvParticipants.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }
        val spaceBetweenItems = resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_8)
        binding.rvParticipants.addItemDecoration(CustomItemDecorator(spaceBetweenItems))
        binding.rvParticipants.setHasFixedSize(true)
    }

    private fun handleState(state: RouletteState) {
        Log.d("MyTag", "handleState: $state")
        when (state) {
            RouletteState.Error -> handleErrorState()
            is RouletteState.Match -> {
                if (isResumed) {
                    handleMatchState(state)
                }
            }

            is RouletteState.Roulette -> handleRouletteState(state)
            is RouletteState.Waiting -> handleWaitingState(state)
            is RouletteState.Init -> handleInitState(state)
        }
    }

    private fun handleErrorState() {
        Log.d("MyTag", "handleErrorState")
        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
    }

    private fun handleMatchState(state: RouletteState.Match) {
        Log.d("MyTag", "handleMatchState")
        val movieDetails = Gson().toJson(state.film)
        val matchBottomSheetFragment = MatchBottomSheetFragment.newInstance(
            movieDetails = movieDetails,
            buttonText = getString(R.string.roulette_to_film_list)
        )
        matchBottomSheetFragment.show(parentFragmentManager, matchBottomSheetFragment.tag)
    }

    private fun handleRouletteState(state: RouletteState.Roulette) {
        Log.d("MyTag", "handleRouletteState: $state")
        if (binding.recyclerViewRoulette.adapter == null) {
            initBottomSheetWaiting(state.users)
            initRecyclerRoulette(state.films)
        }
        binding.recyclerViewRoulette.stopScroll()
        lifecycleScope.launch {
            delay(DELAY_TIME_MS_1000)
            hideBottomSheetWaiting()
            val currentPosition =
                (binding.recyclerViewRoulette.layoutManager as CarouselLayoutManager)
                    .findLastVisibleItemPosition()
            /*
            Порядковый номер последнего видимого элемента прокручиваемого списка (максимум Integer.MAX_VALUE)
            делим и умножаем на количество фильмов.
            Получим количество пройденных элементов, кратное количеству фильмов.
            Прибавляем state.index (это индекс нужного фильма в списке фильмов).
            Для красоты делаем несколько полных оборотов (ROULETTE_SCROLL_COEFFICIENT).
            */
            val position =
                currentPosition / state.count * state.count + state.count * ROULETTE_SCROLL_COEFFICIENT + state.index
            startRouletteScrolling(position)
        }
    }

    private fun handleWaitingState(state: RouletteState.Waiting) {
        Log.d("MyTag", "handleWaitingState")
        if (binding.rvParticipants.adapter == null) {
            initBottomSheetWaiting(state.users)
            initRecyclerRoulette(state.films)
        }
        state.users.forEachIndexed { index, user ->
            if (user.isConnected) {
                (binding.rvParticipants.adapter as UserAdapter).updateItem(index, user)
            }
        }
    }

    private fun handleInitState(state: RouletteState.Init) {
        Log.d("MyTag", "handleInitState")
        initBottomSheetWaiting(state.users)
        initRecyclerRoulette(state.films)
    }

    companion object {
        private const val DELAY_TIME_MS_1000 = 1000L
        private const val ROULETTE_SCROLL_COEFFICIENT = 4
        const val ROULETTE_INITIATOR = "ROULETTE_INITIATOR"
    }
}
