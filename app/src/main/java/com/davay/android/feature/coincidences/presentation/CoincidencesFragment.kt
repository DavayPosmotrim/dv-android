package com.davay.android.feature.coincidences.presentation

import android.os.Bundle
import android.view.View
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
import com.davai.uikit.dialog.MainDialogFragment
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.presentation.LastItemDecorator
import com.davay.android.core.presentation.MainActivity
import com.davay.android.databinding.FragmentCoincidencesBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.coincidences.bottomsheetdialog.RouletteBottomSheetDialogFragment
import com.davay.android.feature.coincidences.di.DaggerCoincidencesFragmentComponent
import com.davay.android.feature.coincidences.presentation.adapter.MoviesGridAdapter
import com.davay.android.utils.presentation.UiErrorHandler
import com.davay.android.utils.presentation.UiErrorHandlerImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("LargeClass")
class CoincidencesFragment : BaseFragment<FragmentCoincidencesBinding, CoincidencesViewModel>(
    FragmentCoincidencesBinding::inflate
) {

    override val viewModel: CoincidencesViewModel by injectViewModel<CoincidencesViewModel>()

    private val errorHandler: UiErrorHandler = UiErrorHandlerImpl()

    private val moviesGridAdapter = MoviesGridAdapter(lifecycleScope) { movieDetails ->
        val action = CoincidencesFragmentDirections
            .actionCoincidencesFragmentToMovieCardFragment(movieDetails)
        viewModel.navigate(action)
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
        ViewCompat.getRootWindowInsets(binding.root)
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
            ?.bottom
            ?.let {
                addItemDecoration(LastItemDecorator(it))
            }
        adapter = moviesGridAdapter
    }

    private fun setupToolbar() {
        binding.toolbarView.apply {
            hideMatchesCounter()
            setEndIconClickListener {
                val coincidences = moviesGridAdapter.itemCount
                if (coincidences >= MIN_COINCIDENCES_FOR_NAVIGATION_3) {
                    val action = CoincidencesFragmentDirections
                        .actionCoincidencesFragmentToRouletteFragment(true)
                    viewModel.navigate(action)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sessionStatusState.collect { state ->
                when (state) {
                    SessionStatus.CLOSED -> {
                        viewModel.getSessionDataAndSaveToDb()
                        showConfirmDialogAtSessionClosedStatus()
                    }

                    SessionStatus.ROULETTE -> showConfirmDialogAndNavigateToRoulette()
                    else -> {}
                }
            }
        }
    }

    private fun showConfirmDialogAtSessionClosedStatus() {
        val dialog = MainDialogFragment.newInstance(
            title = getString(R.string.select_movies_session_is_closed),
            message = getString(R.string.select_movies_user_left_session),
            showConfirmBlock = true,
            yesAction = {
                viewModel.leaveSessionAndNavigateToHistory()
            },
            onCancelAction = {
                viewModel.leaveSessionAndNavigateToHistory()
            }
        )
        dialog.show(parentFragmentManager, null)
    }

    private fun showConfirmDialogAndNavigateToRoulette() {
        val action =
            CoincidencesFragmentDirections.actionCoincidencesFragmentToRouletteFragment(false)
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

    private fun handleState(state: CoincidencesState) {
        when (state) {
            is CoincidencesState.Loading -> showProgressBar()
            is CoincidencesState.Content -> showContent(state)
            is CoincidencesState.Error -> showError(state)
        }
    }

    private fun showProgressBar() = with(binding) {
        progressBar.isVisible = true
        coincidencesList.isVisible = false
        errorScreen.isVisible = false
    }


    private fun showContent(content: CoincidencesState.Content) = with(binding) {
        progressBar.isVisible = false
        coincidencesList.isVisible = true
        errorScreen.isVisible = false
        moviesGridAdapter.setData(content.data)
    }

    private fun showError(error: CoincidencesState.Error) = with(binding) {
        progressBar.isVisible = false
        coincidencesList.isVisible = false
        errorScreen.isVisible = true
        errorHandler.handleError(error.errorType, errorScreen) {
            viewModel.getCoincidences()
        }
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