package com.davay.android.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.extensions.animateBottom
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: Inflate<VB>,
) : BottomSheetDialogFragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation()
    }

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

    // для диалогов с клавиатурой
    protected fun makeDialogWithKeyboard(savedInstanceState: Bundle?): Dialog {
        val thisDialog = super.onCreateDialog(savedInstanceState)
        thisDialog.window?.also {
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            WindowCompat.setDecorFitsSystemWindows(it, false)
        }
        return thisDialog
    }

    // для диалогов с клавиатурой
    protected fun moveBottomView(movingView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView) { _, windowInsets ->
            @Suppress("TooGenericExceptionCaught")
            try {
                val insetsIme = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
                val insetsNav = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                movingView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = if (windowInsets.isVisible(WindowInsetsCompat.Type.ime())) {
                        insetsIme.bottom - insetsNav.bottom
                    } else {
                        0
                    }
                }
            } catch (e: NullPointerException) {
                // ничего не делаем
                e.printStackTrace()
            }
            windowInsets
        }

        animateBottom(
            listenableView = binding.root.rootView,
            startBottomView = binding.root.rootView,
            endBottomView = binding.root,
            animateView = movingView
        )
    }
}