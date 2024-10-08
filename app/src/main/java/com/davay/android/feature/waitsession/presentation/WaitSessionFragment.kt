package com.davay.android.feature.waitsession.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.davai.extensions.dpToPx
import com.davai.uikit.BannerView
import com.davai.uikit.dialog.MainDialogFragment
import com.davai.util.setOnDebouncedClickListener
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.domain.models.SessionShort
import com.davay.android.core.presentation.MainActivity
import com.davay.android.databinding.FragmentWaitSessionBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.createsession.presentation.createsession.CreateSessionViewModel
import com.davay.android.feature.waitsession.di.DaggerWaitSessionFragmentComponent
import com.davay.android.feature.waitsession.presentation.adapter.CustomItemDecorator
import com.davay.android.feature.waitsession.presentation.adapter.UserAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class WaitSessionFragment : BaseFragment<FragmentWaitSessionBinding, WaitSessionViewModel>(
    FragmentWaitSessionBinding::inflate
) {
    override val viewModel: WaitSessionViewModel by injectViewModel<WaitSessionViewModel>()
    private val userAdapter = UserAdapter()
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var session: SessionShort? = null
    private val dialog: MainDialogFragment by lazy {
        MainDialogFragment.newInstance(
            title = getString(R.string.leave_wait_session_title),
            message = getString(R.string.leave_wait_session_dialog_message),
            yesAction = {
                viewModel.navigateToCreateSessionAndUnsubscribeWebSockets()
            }
        )
    }

    override fun diComponent(): ScreenComponent = DaggerWaitSessionFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            session = Json.decodeFromString(it.getString(CreateSessionViewModel.SESSION_DATA) ?: "")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            binding.sendButton.setButtonEnabled(true)
        }
    }

    override fun initViews() {
        initRecycler()
    }

    override fun subscribe() {
        setButtonClickListeners()
        setBackPressedCallback()

        val sessionId = session?.id ?: ""
        binding.llButtonContainer.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            copyTextToClipboard(sessionId)
        }

        binding.sendButton.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            if (it.isEnabled) {
                sendCode(sessionId)
                binding.sendButton.setButtonEnabled(false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                Log.i(TAG, "state: $state")
                renderState(state)
            }
        }
    }

    private fun renderState(state: WaitSessionState) {
        when (state) {
            is WaitSessionState.Content -> {
                userAdapter.setItems(state.users)
                Log.i(TAG, "state.session: ${state.users}")
            }

            is WaitSessionState.Error -> {

            }
        }
    }

    private fun setBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dialog.show(parentFragmentManager, CUSTOM_DIALOG_TAG)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        launcher = null
    }

    private fun copyTextToClipboard(text: String) { //! вынести логику во viewmodel
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            ContextCompat.getString(requireContext(), R.string.wait_session_copy_button_toast_text),
            text
        )
        clipboard.setPrimaryClip(clip)

        updateAndShowBanner(
            getString(R.string.wait_session_copy_button_text),
            BannerView.SUCCESS
        )
    }

    private fun sendCode(text: String) { //! вынести логику во viewmodel
        val part1 = getString(R.string.additional_message_part1)
        val part2 = getString(R.string.additional_message_part2)
        val combinedText = "$part1\n$part2 $text"

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, combinedText)
            type = TEXT_TYPE
        }
        val shareIntent = Intent.createChooser(intent, null)
        launcher?.launch(shareIntent)
    }

    private fun initRecycler() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }
        val spaceBetweenItems = SPACING_BETWEEN_RV_ITEMS_8_DP.dpToPx() //! не всегда срабатывает при добавлении новых юзеров к сессии и ообновлении списка
        binding.rvUser.apply {
            adapter = userAdapter
            layoutManager = flexboxLayoutManager
            addItemDecoration(CustomItemDecorator(spaceBetweenItems))
        }
    }

    private fun setButtonClickListeners() = with(binding) {
        cancelButton.setOnDebouncedClickListener(coroutineScope = lifecycleScope) { // TODO добавить обновление статуса сессии
            dialog.show(parentFragmentManager, CUSTOM_DIALOG_TAG)
        }
        startSessionButton.setOnDebouncedClickListener(
            coroutineScope = lifecycleScope
        ) {
            if (userAdapter.itemCount < MIN_USER_TO_START_2) {
                updateAndShowBanner(
                    getString(R.string.wait_session_min_two_users),
                    BannerView.ATTENTION
                )
            } else {
                viewModel.navigateToNextScreen()
            }
        }
    }

    private fun updateAndShowBanner(text: String, type: Int) {
        val activity = requireActivity() as MainActivity
        activity.updateBanner(text, type)
        activity.showBanner()
    }

    private companion object {
        const val SPACING_BETWEEN_RV_ITEMS_8_DP = 8
        const val CUSTOM_DIALOG_TAG = "customDialog"
        const val MIN_USER_TO_START_2 = 2
        const val TEXT_TYPE = "text/plain"


        val TAG = WaitSessionState::class.simpleName
    }
}