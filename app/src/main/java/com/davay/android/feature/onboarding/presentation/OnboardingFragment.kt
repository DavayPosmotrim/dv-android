package com.davay.android.feature.onboarding.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.davai.util.setOnDebouncedClickListener
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentOnboardingBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.onboarding.adapter.OnboardingViewPagerAdapter
import com.davay.android.feature.onboarding.di.DaggerOnBoardingFragmentComponent
import com.davay.android.feature.onboarding.viewmodel.OnboardingViewModel

/**
 * Фрагмент переписан, распаковывает bundle и на основании параметра инфлэйтит viewpager,
 * дефолтно инфлейтит ресурсы для онбординга с инструкцией
 */
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding, OnboardingViewModel>(
    FragmentOnboardingBinding::inflate
) {
    override val viewModel: OnboardingViewModel by injectViewModel<OnboardingViewModel>()
    private val dataProvider = OnboardingDataProvider()

    private var onboardingItems = dataProvider.getInstructionOnboardingData()
    private val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: OnboardingFragmentArgs by navArgs()
        if (args.setNumber == ONBOARDING_MAIN_SET) {
            onboardingItems = dataProvider.getMainOnboardingData()
        }
    }

    override fun diComponent(): ScreenComponent =
        DaggerOnBoardingFragmentComponent.builder().appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        setUpButtonClickListener()
    }

    override fun initViews() = with(binding) {
        fragmentList.clear()
        fragmentList.addAll(
            onboardingItems.filter { item ->
                item.textResId != null || item.imageResId != null || item.descriptionResId != null
            }.map { item ->
                OnboardingFirstFragment.newInstance(item)
            }
        )
        viewpager.adapter = OnboardingViewPagerAdapter(
            fragmentList, viewLifecycleOwner.lifecycle, this@OnboardingFragment.childFragmentManager
        )
        ciIndicator.setViewPager(viewpager)
    }


    private fun setUpViewPager() {
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonTextForFragment(position)
            }
        })
    }

    private fun updateButtonTextForFragment(position: Int) {
        binding.mbtnFooterBtn.text = when (position) {
            fragmentList.lastIndex -> resources.getString(R.string.onboarding_begin)
            else -> resources.getString(R.string.onboarding_continue_view)
        }
    }

    private fun setUpButtonClickListener() {
        binding.mbtnFooterBtn.setOnDebouncedClickListener(coroutineScope = lifecycleScope) {
            val currentFragment = binding.viewpager.currentItem
            val nextFragment = currentFragment + 1
            if (nextFragment < fragmentList.size) {
                binding.viewpager.setCurrentItem(nextFragment, true)
            } else {
                val lastItem = onboardingItems.last()
                lastItem.action?.let { action ->
                    viewModel.navigate(action)
                }
            }
        }
    }

    companion object {
        const val ONBOARDING_MAIN_SET = 0
        const val ONBOARDING_INSTRUCTION_SET = 1
    }
}