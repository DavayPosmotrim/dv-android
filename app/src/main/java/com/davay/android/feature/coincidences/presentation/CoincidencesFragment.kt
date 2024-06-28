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
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentCoincidencesBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.coincidences.bottomsheetdialog.RouletteBottomSheetDialogFragment
import com.davay.android.feature.coincidences.di.DaggerCoincidencesFragmentComponent
import com.davay.android.feature.coincidences.presentation.adapter.MoviesGridAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CoincidencesFragment : BaseFragment<FragmentCoincidencesBinding, CoincidencesViewModel>(
    FragmentCoincidencesBinding::inflate
) {

    override val viewModel: CoincidencesViewModel by injectViewModel<CoincidencesViewModel>()

    private val moviesGridAdapter = MoviesGridAdapter { movieId ->
        Toast.makeText(requireContext(), "Clicked!", Toast.LENGTH_SHORT).show()
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
        subscribe()

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
        if (viewModel.isHintShown()) {
            val existingFragment = parentFragmentManager.findFragmentByTag(
                RouletteBottomSheetDialogFragment::class.java.simpleName
            )
            if (existingFragment is RouletteBottomSheetDialogFragment) {
                rouletteBottomSheetDialogFragment = existingFragment
            }
        } else {
            showBottomSheetDialogFragment()
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
            addStatusBarSpacer()
            setEndIconClickListener {
                Toast.makeText(requireContext(), "Navigate to random.", Toast.LENGTH_SHORT).show()
            }
            setStartIconClickListener {
                viewModel.navigateBack()
            }
        }
    }

    private fun subscribe() {
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
}