package com.davay.android.feature.splash.presentation

import android.content.Context.SENSOR_SERVICE
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentSplashBinding
import com.davay.android.di.AppComponentHolder
import com.davay.android.di.ScreenComponent
import com.davay.android.extensions.setSplashTextViewStyle
import com.davay.android.feature.onboarding.presentation.OnboardingFragment
import com.davay.android.feature.splash.di.DaggerSplashFragmentComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(FragmentSplashBinding::inflate),
    SensorEventListener2 {
    override val viewModel: SplashViewModel by injectViewModel<SplashViewModel>()
    private val sensorManager: SensorManager by lazy {
        requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun diComponent(): ScreenComponent = DaggerSplashFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSensorAccelerometer()
        val bundle = Bundle().apply {
            putInt(OnboardingFragment.ONBOARDING_KEY, OnboardingFragment.ONBOARDING_MAIN_SET)
        }
        lifecycleScope.launch {
            addTextViewsWithDelay()
            delay(DELAY_4000_MS)
            if (viewModel.isFirstTimeLaunch() || viewModel.isNotRegistered()) {
                viewModel.markFirstTimeLaunch()
                viewModel.navigate(R.id.action_splashFragment_to_onboardingFragment, bundle)
            } else {
                viewModel.navigate(R.id.action_splashFragment_to_mainFragment)
            }
        }
    }

    private fun getSensorAccelerometer() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val gravityX = -it.values[0]
            val gravityY = it.values[1]

            binding.physicsLayout.apply {
                physics.setGravity(gravityX, gravityY)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onFlushCompleted(sensor: Sensor?) = Unit

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager.unregisterListener(this)
    }

    private suspend fun addTextViewsWithDelay() {
        val stringArrayResId = R.array.splash_genres

        val stringResIds = resources.getStringArray(stringArrayResId).toList()

        for ((index, text) in stringResIds.shuffled().withIndex()) {
            delay(index * DELAY_10_MS)
            addTextViewToPhysicsLayout(text)
        }
    }

    private fun addTextViewToPhysicsLayout(title: String) {
        val textView = TextView(requireContext()).apply {
            text = title
            setTextAppearance(R.style.Text_Base_SplashItem)
            if (title == resources.getString(R.string.splash_watch)) {
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.davai.uikit.R.color.text_light
                    )
                )
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        com.davai.uikit.R.color.primary_base
                    )
                )
            }
            setSplashTextViewStyle()
        }
        binding.physicsLayout.addView(textView)
    }

    companion object {
        const val DELAY_10_MS = 10L
        const val DELAY_4000_MS = 4000L
        const val START_POSITIONS_NUMBER = 3
        const val MAX_ANGLE_3000 = 3000
        const val MAX_NEGATIVE_ANGLE_3000 = -3000
    }
}
