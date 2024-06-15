package com.davay.android.feature.load.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.davai.uikit.progressbutton.ProgressButtonView
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentLoadBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.load.di.DaggerLoadFragmentComponent
import com.davay.android.feature.onboarding.presentation.OnboardingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadFragment : BaseFragment<FragmentLoadBinding, LoadViewModel>(
    FragmentLoadBinding::inflate
) {

    override val viewModel: LoadViewModel by injectViewModel<LoadViewModel>()
    override fun diComponent(): ScreenComponent =
        DaggerLoadFragmentComponent.builder().appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(
            inflater,
            container,
            savedInstanceState
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener { _ ->
            viewModel.navigate(R.id.action_loadFragment_to_mainFragment)
        }
        binding.btnToMovieSelection.setOnClickListener {
            viewModel.navigate(R.id.action_loadFragment_to_selectMovieFragment)
        }
        binding.button2.setOnClickListener { _ ->
            viewModel.navigate(R.id.action_loadFragment_to_registrationFragment)
        }
        binding.btnToOnboarding.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(OnboardingFragment.ONBOARDING_KEY, OnboardingFragment.ONBOARDING_MAIN_SET)
            }
            navigate(R.id.action_loadFragment_to_onboardingFragment, bundle)
        }

        binding.btnToSplash.setOnClickListener {
            viewModel.navigate(R.id.action_loadFragment_to_splashFragment)
        }

        binding.ccpProgressButton.apply {
            cornerRadius = resources.getDimensionPixelSize(com.davai.uikit.R.dimen.card_radius_24)
        }.also {
            lifecycleScope.launch {
                delay(ProgressButtonView.MS_IN_SECOND)
                it.startAnimation(this)
            }
        }
    }
}