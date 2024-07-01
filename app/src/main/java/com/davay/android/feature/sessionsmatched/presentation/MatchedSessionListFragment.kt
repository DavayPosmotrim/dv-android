package com.davay.android.feature.sessionsmatched.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentMatchedSessionListBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.sessionsmatched.di.DaggerMatchedSessionListFragmentComponent

class MatchedSessionListFragment :
    BaseFragment<FragmentMatchedSessionListBinding, MatchedSessionViewModel>(
        FragmentMatchedSessionListBinding::inflate
    ) {
    override val viewModel: MatchedSessionViewModel by injectViewModel<MatchedSessionViewModel>()
    override fun diComponent(): ScreenComponent =
        DaggerMatchedSessionListFragmentComponent.builder()
            .appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_matched_session_list,
            container,
            false
        )
    }
}