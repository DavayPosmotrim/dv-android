package com.davay.android.feature.sessionlist.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.davai.extensions.dpToPx
import com.davai.uikit.dialog.MainDialogFragment
import com.davai.util.setOnDebouncedClickListener
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentSessionListBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.sessionlist.di.DaggerSessionListFragmentComponent
import com.davay.android.feature.sessionlist.presentation.adapter.CustomItemDecorator
import com.davay.android.feature.sessionlist.presentation.adapter.UserAdapter
import com.davay.android.utils.presentation.UiErrorHandler
import com.davay.android.utils.presentation.UiErrorHandlerImpl
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch

class SessionListFragment : BaseFragment<FragmentSessionListBinding, SessionListViewModel>(
    FragmentSessionListBinding::inflate
) {
    override val viewModel: SessionListViewModel by injectViewModel<SessionListViewModel>()
    private val userAdapter = UserAdapter()
    private var etCode: String? = null
    private var dialog: MainDialogFragment? = null
    private val errorHandler: UiErrorHandler = UiErrorHandlerImpl()

    override fun diComponent(): ScreenComponent = DaggerSessionListFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            etCode = it.getString(ET_CODE_KEY)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dialog?.show(parentFragmentManager, CUSTOM_DIALOG_TAG)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = MainDialogFragment.newInstance(
            title = getString(R.string.leave_session_title),
            message = getString(R.string.leave_session_dialog_message),
            yesAction = {
                etCode?.let { viewModel.leaveSessionAndNavigateBack(it) }
            }
        )
    }

    override fun initViews() {
        setupToolbar()
        initRecycler()
    }

    override fun subscribe() {
        setButtonClickListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                renderState(state)
            }
        }
        viewModel.connectToSessionAuto(etCode.toString())
    }

    private fun setupToolbar() {
        binding.toolbar.setTitleText(getString(R.string.session_list_name) + " " + etCode)
    }

    private fun initRecycler() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }
        val spaceBetweenItems = SPACING_BETWEEN_RV_ITEMS_8_DP.dpToPx()

        binding.rvUser.apply {
            adapter = userAdapter
            layoutManager = flexboxLayoutManager
            addItemDecoration(CustomItemDecorator(spaceBetweenItems))
        }
    }

    private fun setButtonClickListeners() {
        binding.btnExit.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            dialog?.show(parentFragmentManager, CUSTOM_DIALOG_TAG)
        }
    }

    private fun renderState(state: ConnectToSessionState) {
        when (state) {
            is ConnectToSessionState.Loading -> {
                binding.prBar.visibility = View.VISIBLE
            }

            is ConnectToSessionState.Content -> {
                userAdapter.setItems(state.session.users)
                with(binding) {
                    prBar.visibility = View.GONE
                    errorMessage.visibility = View.GONE
                    rvUser.visibility = View.VISIBLE
                    ivSessionListPlaceholder.visibility = View.VISIBLE
                }
            }

            is ConnectToSessionState.Error -> {
                handleError(state)
            }
        }
    }

    private fun handleError(state: ConnectToSessionState.Error) {
        showErrorMessage()
        errorHandler.handleError(
            state.errorType,
            binding.errorMessage
        ) {
            etCode?.let { viewModel.connectToSession(it) }
        }
    }

    private fun showErrorMessage() = with(binding) {
        errorMessage.isVisible = true
        prBar.isVisible = false
        rvUser.isVisible = false
        ivSessionListPlaceholder.isVisible = false
    }

    companion object {
        private const val SPACING_BETWEEN_RV_ITEMS_8_DP = 8
        private const val CUSTOM_DIALOG_TAG = "customDialog"
        private const val ET_CODE_KEY = "ET_CODE_KEY"
    }
}
