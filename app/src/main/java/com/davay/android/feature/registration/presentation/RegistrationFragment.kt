package com.davay.android.feature.registration.presentation

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.UserNameState
import com.davay.android.core.presentation.MainActivity
import com.davay.android.databinding.FragmentRegistrationBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.extensions.animateBottom
import com.davay.android.feature.registration.di.DaggerRegistrationFragmentComponent
import kotlinx.coroutines.launch

class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding, RegistrationViewModel>(FragmentRegistrationBinding::inflate) {

    override val viewModel: RegistrationViewModel by injectViewModel<RegistrationViewModel>()

    override fun diComponent(): ScreenComponent = DaggerRegistrationFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setKeyBoardInsets(binding.root)
        animateBottom(
            listenableView = binding.root,
            startBottomView = binding.btnEnter,
            endBottomView = binding.btnEnter,
            animateView = binding.root
        )
        showSoftKeyboard(binding.etName)
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
            viewModel.navigateBack()
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(requireContext(), InputMethodManager::class.java)
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun stateHandle(state: UserNameState?) = with(binding) {
        val isLoading = state == UserNameState.LOADING
        progressBar.isVisible = isLoading
        etName.isEnabled = isLoading.not()
        tvErrorHint.text = state?.getMessage(requireContext()) ?: ""
        if (state == UserNameState.SUCCESS) {
            viewModel.navigate(R.id.action_registrationFragment_to_mainFragment)
        }
    }

    private fun setButtonClickListeners() {
        binding.btnEnter.setOnClickListener {
            viewModel.buttonClicked(binding.etName.text)
        }
        binding.etName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.buttonClicked(binding.etName.text)
                true
            } else {
                false
            }
        }
    }

    companion object {
        private const val TYPE_SMALL_BORDER = 12
    }
}
