package com.davay.android.feature.sessionsmatched.presentation

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.davai.extensions.dpToPx
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.Session
import com.davay.android.databinding.FragmentMatchedSessionListBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.matchedsession.presentation.MatchedSessionFragment
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
            toolbarSessions.setStartIconClickListener {
                viewModel.navigateBack()
            }
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
            is MatchedSessionsState.Content -> showContent(state.sessionsList)
        }
    }

    private fun showContent(sessions: List<Session>) {
        binding.rvSessionList.isVisible = true
        sessionsAdapter.setData(sessions)
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
        val bundle = Bundle().apply {
            putString(MatchedSessionFragment.SESSION_ID, id)
        }
        viewModel.navigate(R.id.action_matchedSessionListFragment_to_matchedSessionFragment, bundle)
    }
}