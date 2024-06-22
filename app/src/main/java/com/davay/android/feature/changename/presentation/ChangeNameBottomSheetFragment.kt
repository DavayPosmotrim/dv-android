package com.davay.android.feature.changename.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseBottomSheetFragment
import com.davay.android.databinding.FragmentNameChangeBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.changename.di.DaggerChangeNameFragmentComponent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

class ChangeNameBottomSheetFragment :
    BaseBottomSheetFragment<FragmentNameChangeBinding, ChangeNameViewModel>(
        FragmentNameChangeBinding::inflate
    ) {

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var name: String? = null

    override val viewModel: ChangeNameViewModel by injectViewModel<ChangeNameViewModel>()

    override fun diComponent(): ScreenComponent = DaggerChangeNameFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME)
        }
        savedInstanceState?.let {
            name = it.getString(ARG_NAME)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val thisDdailog = super.onCreateDialog(savedInstanceState)
        thisDdailog.window?.also {
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            WindowCompat.setDecorFitsSystemWindows(it, false)
        }
        return thisDdailog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name?.let {
            binding.etName.setText(it)
        }

        lifecycleScope.launch {
            viewModel.state.collect { stateHandle(it) }
        }

        setButtonClickListeners()
        binding.etName.doAfterTextChanged {
            viewModel.textCheck(it)
            if (it?.length!! >= TYPE_SMALL_BORDER) {
                binding.etName.setTextAppearance(com.davai.uikit.R.style.Text_Headline_SubTitle)
                binding.etName.setTextColor(
                    resources.getColor(
                        com.davai.uikit.R.color.text_base,
                        requireActivity().theme
                    )
                )
            } else {
                binding.etName.setTextAppearance(com.davai.uikit.R.style.Text_Headline_Title)
                binding.etName.setTextColor(
                    resources.getColor(
                        com.davai.uikit.R.color.text_base,
                        requireActivity().theme
                    )
                )
            }
        }
        binding.etName.buttonBackHandler = {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }
        buildBottomSheet()
        animateKeyboard()
        showSoftKeyboard(binding.etName)
    }

    private fun buildBottomSheet() {
        val parentView = view?.parent as? View ?: return
        bottomSheetBehavior = BottomSheetBehavior.from(parentView)

        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                val desiredHeight = (screenHeight * BOTTOM_SHEET_HEIGHT).toInt()

                parentView.layoutParams.height = desiredHeight
                parentView.requestLayout()

                bottomSheetBehavior?.peekHeight = desiredHeight
                parentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    showSoftKeyboard(binding.etName)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset < BOTTOM_SHEET_HIDE_PERCENT_60) {
                    hideKeyboard(binding.etName)
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        })
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm =
            ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun stateHandle(state: ChangeNameState?) {
        binding.tvErrorHint.text = when (state) {
            ChangeNameState.FIELD_EMPTY -> resources.getString(R.string.registration_enter_name)
            ChangeNameState.MINIMUM_LETTERS -> resources.getString(R.string.registration_two_letters_minimum)
            ChangeNameState.NUMBERS -> resources.getString(R.string.registration_just_letters)
            ChangeNameState.SUCCESS, ChangeNameState.DEFAULT, null -> ""
            ChangeNameState.MAXIMUM_LETTERS -> resources.getString(R.string.registration_not_more_letters)
        }
    }

    private fun setButtonClickListeners() {
        binding.btnEnter.setOnClickListener {
            buttonClicked()
        }
        binding.etName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                buttonClicked()
                true
            } else {
                false
            }
        }
    }

    private fun buttonClicked() {
        viewModel.buttonClicked(binding.etName.text)
        if (viewModel.state.value == ChangeNameState.SUCCESS) {
            val newName = binding.etName.text.toString()
            setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_NAME to newName))
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun animateKeyboard() {
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView) { _, windowInsets ->
            try {
                var insetsIme = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
                val insetsNav = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                if (windowInsets.isVisible(WindowInsetsCompat.Type.ime())) {
                    binding.btnEnter.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = insetsIme.bottom - insetsNav.bottom
                    }
                } else {
                    binding.btnEnter.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = 0
                    }
                }
            } catch (e: NullPointerException) {
                // так как по нажатию кнопки никуда не переходим, то можно без try-catch
                e.printStackTrace()
            }
            windowInsets
        }

        // api 30+
        ViewCompat.setWindowInsetsAnimationCallback(
            requireView().rootView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                var startBottom = 0f
                var endBottom = 0f

                override fun onPrepare(
                    animation: WindowInsetsAnimationCompat
                ) {
                    startBottom = requireView().rootView.bottom.toFloat()
                }

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    endBottom = binding.root.bottom.toFloat()
                    return bounds
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeAnimation = runningAnimations.find {
                        it.typeMask and WindowInsetsCompat.Type.ime() != 0
                    } ?: return insets
                    binding.btnEnter.translationY =
                        (startBottom - endBottom) * (1 - imeAnimation.interpolatedFraction)
                    return insets
                }
            }
        )
    }

    companion object {
        private const val TYPE_SMALL_BORDER = 12
        private const val BOTTOM_SHEET_HIDE_PERCENT_60 = 0.6f
        private const val BOTTOM_SHEET_HEIGHT = 0.9f

        private const val ARG_NAME = "name"
        const val REQUEST_KEY = "changeNameRequestKey"
        const val BUNDLE_KEY_NAME = "changedName"

        fun newInstance(name: String) = ChangeNameBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NAME, name)
            }
        }
    }
}