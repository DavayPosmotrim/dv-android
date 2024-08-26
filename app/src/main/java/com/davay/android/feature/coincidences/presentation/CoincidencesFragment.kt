package com.davay.android.feature.coincidences.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.davai.uikit.BannerView
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.presentation.MainActivity
import com.davay.android.databinding.FragmentCoincidencesBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.coincidences.bottomsheetdialog.RouletteBottomSheetDialogFragment
import com.davay.android.feature.coincidences.di.DaggerCoincidencesFragmentComponent
import com.davay.android.feature.coincidences.presentation.adapter.MoviesGridAdapter
import com.davay.android.feature.moviecard.presentation.MovieCardFragment
import com.davay.android.feature.roulette.presentation.RouletteFragment.Companion.ROULETTE_INITIATOR
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CoincidencesFragment : BaseFragment<FragmentCoincidencesBinding, CoincidencesViewModel>(
    FragmentCoincidencesBinding::inflate
) {

    override val viewModel: CoincidencesViewModel by injectViewModel<CoincidencesViewModel>()

    private val moviesGridAdapter = MoviesGridAdapter { movieDetails ->
        val movie = Json.encodeToString(movieDetails)
        val bundle = Bundle().apply {
            putString(MovieCardFragment.MOVIE_DETAILS_KEY, movie)
        }
        viewModel.navigate(R.id.action_coincidencesFragment_to_movieCardFragment, bundle)
    }

    private val bottomSheetFragmentLifecycleCallbacks =
        object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentCreated(
                fm: FragmentManager,
                f: Fragment,
                savedInstanceState: Bundle?
            ) {
                super.onFragmentCreated(fm, f, savedInstanceState)
                if (f is RouletteBottomSheetDialogFragment) {
                    showPointersAndShadow()
                }
            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                if (f is RouletteBottomSheetDialogFragment) {
                    hidePointersAndShadow()
                }
            }
        }

    private var rouletteBottomSheetDialogFragment: RouletteBottomSheetDialogFragment? = null

    override fun diComponent(): ScreenComponent = DaggerCoincidencesFragmentComponent
        .builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupMoviesGrid()

        // При смене конфигурации не нужно показывать подсказку
        if (savedInstanceState == null) {
            showHint()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentFragmentManager.unregisterFragmentLifecycleCallbacks(
            bottomSheetFragmentLifecycleCallbacks
        )
    }

    /**
     * Показывает подсказку, если она еще не показана
     */
    private fun showHint() {
        if (viewModel.isFirstTimeLaunch()) {
            showBottomSheetDialogFragment()
            viewModel.markFirstTimeLaunch()
        } else {
            val existingFragment = parentFragmentManager.findFragmentByTag(
                RouletteBottomSheetDialogFragment::class.java.simpleName
            )
            if (existingFragment is RouletteBottomSheetDialogFragment) {
                rouletteBottomSheetDialogFragment = existingFragment
            }
        }

        parentFragmentManager.registerFragmentLifecycleCallbacks(
            bottomSheetFragmentLifecycleCallbacks,
            true
        )
    }

    private fun setupMoviesGrid() = with(binding.coincidencesList) {
        adapter = moviesGridAdapter
    }

    private fun setupToolbar() {
        binding.toolbarView.apply {
            hideMatchesCounter()
            setEndIconClickListener {
                val coincidences = viewModel.getCoincidencesCount()
                if (coincidences >= MIN_COINCIDENCES_FOR_NAVIGATION_3) {
                    val bundle = Bundle().apply {
                        putBoolean(ROULETTE_INITIATOR, true)
                    }
                    viewModel.navigate(R.id.action_coincidencesFragment_to_rouletteFragment, bundle)
                } else {
                    val activity = requireActivity() as MainActivity
                    activity.updateBanner(
                        getString(R.string.coincidences_screen_start_roulette),
                        BannerView.ATTENTION
                    )
                    activity.showBanner()
                }
            }
            setStartIconClickListener {
                viewModel.navigateBack()
            }
        }
    }

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

    private fun showBottomSheetDialogFragment() {
        if (rouletteBottomSheetDialogFragment == null) {
            rouletteBottomSheetDialogFragment = RouletteBottomSheetDialogFragment()
            rouletteBottomSheetDialogFragment?.show(
                parentFragmentManager,
                RouletteBottomSheetDialogFragment::class.java.simpleName
            )
        }
    }

    private fun showPointersAndShadow() = with(binding) {
        listOf(ivFigureArrow, viewDialogShadow).forEach {
            it.visibility = View.VISIBLE
            addStatusBarSpacerAndShowPointer(requireView())
        }
    }

    private fun hidePointersAndShadow() = with(binding) {
        listOf(ivDices, ivWhitePointer, ivFigureArrow, viewDialogShadow).forEach {
            it.visibility = View.GONE
        }
    }

    private fun addStatusBarSpacerAndShowPointer(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            applySpaceHeightAndShowPointer(statusBarHeight)
            insets
        }
        view.requestApplyInsets()
    }

    private fun applySpaceHeightAndShowPointer(statusBarHeight: Int) = with(binding) {
        topSpace.let {
            val layoutParams = it.layoutParams
            layoutParams.height = statusBarHeight
            it.layoutParams = layoutParams
        }

        ivWhitePointer.post {
            val layoutParams = ivWhitePointer.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = statusBarHeight
            ivWhitePointer.apply {
                visibility = View.VISIBLE
                this.layoutParams = layoutParams
            }
            ivDices.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val MIN_COINCIDENCES_FOR_NAVIGATION_3 = 3
    }
}