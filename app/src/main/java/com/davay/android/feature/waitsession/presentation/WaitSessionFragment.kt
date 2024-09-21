package com.davay.android.feature.waitsession.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.davai.extensions.dpToPx
import com.davai.uikit.BannerView
import com.davai.uikit.ButtonView
import com.davai.uikit.MainDialogFragment
import com.davai.util.setOnDebouncedClickListener
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.core.presentation.MainActivity
import com.davay.android.databinding.FragmentWaitSessionBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.onboarding.presentation.OnboardingFragment
import com.davay.android.feature.waitsession.di.DaggerWaitSessionFragmentComponent
import com.davay.android.feature.waitsession.presentation.adapter.CustomItemDecorator
import com.davay.android.feature.waitsession.presentation.adapter.UserAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class WaitSessionFragment : BaseFragment<FragmentWaitSessionBinding, WaitSessionViewModel>(
    FragmentWaitSessionBinding::inflate
) {
    override val viewModel: WaitSessionViewModel by injectViewModel<WaitSessionViewModel>()
    private val userAdapter = UserAdapter()
    private var sendButton: ButtonView? = null
    private var dialog: MainDialogFragment? = null
    private var launcher: ActivityResultLauncher<Intent>? = null

    private val args: WaitSessionFragmentArgs by navArgs()

    override fun diComponent(): ScreenComponent = DaggerWaitSessionFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dialog?.show(parentFragmentManager, CUSTOM_DIALOG_TAG)
                }
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            sendButton?.setButtonEnabled(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCode.text = args.session.id
        sendButton = binding.sendButton


        initRecycler()
        userAdapter.setItems(
            listOf("Артем", "Руслан", "Константин", "Виктория")
        )

        binding.llButtonContainer.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            val code = args.session.id
            copyTextToClipboard(code)
        }

        sendButton?.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            val code = args.session.id
            if (it.isEnabled) {
                sendCode(code)
                sendButton?.setButtonEnabled(false)
            }
        }

        dialog = MainDialogFragment.newInstance(
            title = getString(R.string.leave_wait_session_title),
            message = getString(R.string.leave_wait_session_dialog_message),
            yesAction = {
                /**
                 * Вместо popBackStack используется именно такая навигация для обхода ошибки при возврате назад на экран создания сессии после
                 * смены конфигурации устройства
                 */
                viewModel.clearBackStackToMainAndNavigate(
                    WaitSessionFragmentDirections.actionWaitSessionFragmentToCreateSessionFragment()
                )
            }
        )
    }

    override fun subscribe() {
        setButtonClickListeners()
    }

    override fun onDetach() {
        super.onDetach()
        launcher = null
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            ContextCompat.getString(requireContext(), R.string.wait_session_copy_button_toast_text),
            text
        )
        clipboard.setPrimaryClip(clip)

        updateBanner(
            getString(R.string.wait_session_copy_button_text),
            BannerView.SUCCESS
        )
        showBanner()
    }

    private fun sendCode(text: String) {
        val part1 = getString(R.string.additional_message_part1)
        val part2 = getString(R.string.additional_message_part2)
        val combinedText = "$part1\n$part2 $text"

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, combinedText)
            type = "text/plain"
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
        val spaceBetweenItems = SPACING_BETWEEN_RV_ITEMS_8_DP.dpToPx()
        binding.rvUser.apply {
            adapter = userAdapter
            layoutManager = flexboxLayoutManager
            addItemDecoration(CustomItemDecorator(spaceBetweenItems))
        }
    }

    private fun setButtonClickListeners() = with(binding) {
        cancelButton.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            dialog?.show(parentFragmentManager, CUSTOM_DIALOG_TAG)
        }
        startSessionButton.setOnDebouncedClickListener(
            coroutineScope = lifecycleScope
        ) {
            if (userAdapter.itemCount < MIN_USER_TO_START_2) {
                showAttentionBanner()
            } else {
                if (viewModel.isFirstTimeLaunch()) {
                    viewModel.markFirstTimeLaunch()
                    val action = WaitSessionFragmentDirections
                        .actionWaitSessionFragmentToOnboardingFragment(OnboardingFragment.ONBOARDING_INSTRUCTION_SET)
                    viewModel.navigate(action)
                } else {
                    val action = WaitSessionFragmentDirections
                        .actionWaitSessionFragmentToSelectMovieFragment()
                    viewModel.navigate(action)
                }
            }
        }
    }

    private fun showAttentionBanner() {
        updateBanner(getString(R.string.wait_session_min_two_user), BannerView.ATTENTION)
        showBanner()
    }

    private fun updateBanner(text: String, type: Int) {
        (requireActivity() as MainActivity).updateBanner(text, type)
    }

    private fun showBanner() {
        val activity = requireActivity() as? MainActivity
        activity?.showBanner()
    }

    private companion object {
        const val SPACING_BETWEEN_RV_ITEMS_8_DP = 8
        const val CUSTOM_DIALOG_TAG = "customDialog"
        const val MIN_USER_TO_START_2 = 2
    }
}