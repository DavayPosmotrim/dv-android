package com.davay.android.feature.sessionsmatched.presentation

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.davai.extensions.dpToPx
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentMatchedSessionListBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.domain.models.ErrorType
import com.davay.android.domain.models.Session
import com.davay.android.feature.sessionsmatched.di.DaggerMatchedSessionListFragmentComponent
import com.davay.android.feature.sessionsmatched.presentation.adapters.SessionListAdapter
import com.davay.android.feature.sessionsmatched.presentation.adapters.TopSpacingItemDecoration
import kotlinx.coroutines.launch

class MatchedSessionListFragment :
    BaseFragment<FragmentMatchedSessionListBinding, MatchedSessionsViewModel>(
        FragmentMatchedSessionListBinding::inflate
    ) {
    override val viewModel: MatchedSessionsViewModel by injectViewModel<MatchedSessionsViewModel>()
    override fun diComponent(): ScreenComponent =
        DaggerMatchedSessionListFragmentComponent.builder()
            .appComponent(AppComponentHolder.getComponent())
            .build()

    private val sessionsAdapter = SessionListAdapter { id ->
        navigateToSessionMovies(id)
    }

    override fun subscribe() {
        super.subscribe()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                render(state)
            }
        }
    }

    override fun initViews() {
        with(binding) {
            super.initViews()
            toolbarSessions.addStatusBarSpacer()
            rvSessionList.apply {
                adapter = sessionsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(TopSpacingItemDecoration(16.dpToPx()))
            }
        }
    }

    private fun render(state: MatchedSessionsState) {
        when (state) {
            is MatchedSessionsState.Empty -> showEmpty()
            is MatchedSessionsState.Loading -> showLoading()
            is MatchedSessionsState.Error -> showError(state.errorType)
            is MatchedSessionsState.Content -> showContent(state.sessionsList)
        }
    }

    private fun showContent(sessions: List<Session>) {
        binding.rvSessionList.isVisible = true
        sessionsAdapter.setData(sessions)
    }

    private fun showError(errorType: ErrorType) {
        Toast.makeText(requireContext(), errorType.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun showEmpty() = with(binding) {
        rvSessionList.isVisible = false
        llStateErrorAndProgressParent.isVisible = true
        emptyPlaceholder.root.isVisible = true
        tvErrorEmptyMessage.isVisible = true
        progressBar.isVisible = false
    }

    private fun showLoading() = with(binding) {
        rvSessionList.isVisible = false
        llStateErrorAndProgressParent.isVisible = true
        emptyPlaceholder.root.isVisible = false
        tvErrorEmptyMessage.isVisible = false
        progressBar.isVisible = true
    }

    private fun navigateToSessionMovies(id: String) {
        Toast.makeText(requireContext(), "Navigate to movie id $id", Toast.LENGTH_SHORT).show()
    }
}