package com.davay.android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.createsession.presentation.createsession.CreateSessionFragment
import com.davay.android.feature.onboarding.presentation.OnboardingFragment
import com.davay.android.feature.registration.presentation.RegistrationFragment
import com.davay.android.feature.roulette.presentation.RouletteFragment
import com.davay.android.feature.sessionlist.presentation.SessionListFragment
import com.davay.android.feature.waitsession.presentation.WaitSessionFragment

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: Inflate<VB>,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    abstract val viewModel: VM

    open val viewModelFactory: ViewModelProvider.Factory by lazy {
        with(diComponent()) {
            viewModelFactory
        }
    }

    protected abstract fun diComponent(): ScreenComponent

    inline fun <reified VM : BaseViewModel> injectViewModel() = viewModels<VM>(
        factoryProducer = { viewModelFactory }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeNavigation()
        subscribe()
        setBottomInsets()
    }

    private fun observeNavigation() {
        viewModel.navigation.observeNonNull(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { navigationCommand ->
                handleNavigation(navigationCommand)
            }
        }
    }

    private fun handleNavigation(navCommand: NavigationCommand) {
        when (navCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(
                navCommand.directions,
                navCommand.navOptions
            )

            is NavigationCommand.Back -> findNavController().navigateUp()
        }
    }

    protected open fun initViews() = Unit

    protected open fun subscribe() = Unit

    private fun setBottomInsets() {
        when (this::class) {
            in screensWithoutEdgeSupport -> {
                ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                    insets
                }
            }

            else -> {
                ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, 0, systemBars.right, 0)
                    insets
                }
            }
        }
    }
}

private val screensWithoutEdgeSupport = setOf(
    CreateSessionFragment::class,
    RouletteFragment::class,
    SessionListFragment::class,
    WaitSessionFragment::class,
    RegistrationFragment::class,
    OnboardingFragment::class,
)
