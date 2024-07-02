package com.davay.android.feature.onboarding.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.davay.android.R
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentOnboardingBinding
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
    private var arrayOfIds =
        dataProvider.getInstructionOnboardingData() // дефолтно устанавливаем контент онбординга с инструкциями
    private val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val setNumber = it.getInt(ONBOARDING_KEY, ONBOARDING_INSTRUCTION_SET)
            if (setNumber == ONBOARDING_MAIN_SET) {
                arrayOfIds = dataProvider.getMainOnboardingData()
            }
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
        fragmentList.addAll(
            listOf(
                OnboardingFirstFragment(arrayOfIds[0]),
                OnboardingFirstFragment(arrayOfIds[1]),
                OnboardingFirstFragment(arrayOfIds[2])
            )
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
        binding.mbtnFooterBtn.setOnClickListener {
            val currentFragment = binding.viewpager.currentItem
            val nextFragment = currentFragment + 1
            if (nextFragment < fragmentList.size) {
                binding.viewpager.setCurrentItem(nextFragment, true)
            } else {
                viewModel.navigate(arrayOfIds.last()[0])
            }
        }
    }

    companion object {
        const val ONBOARDING_KEY = "onboarding_key"
        const val ONBOARDING_MAIN_SET = 0
        const val ONBOARDING_INSTRUCTION_SET = 1
    }
}