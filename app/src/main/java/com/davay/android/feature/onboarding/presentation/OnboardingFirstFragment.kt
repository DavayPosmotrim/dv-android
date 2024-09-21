package com.davay.android.feature.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.davay.android.R

class OnboardingFirstFragment : Fragment() {

    private val args: OnboardingFirstFragmentArgs by navArgs()
    private val onboardingItem: OnboardingItem by lazy { args.onboardingItem }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_onboarding_first,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onboardingItem.let { item ->
            item.textResId?.let { view.findViewById<TextView>(R.id.tv_top_title).setText(it) }
            item.imageResId?.let {
                view.findViewById<ImageView>(R.id.iv_main_image).setImageResource(it)
            }
            item.descriptionResId?.let {
                view.findViewById<TextView>(R.id.tv_bottom_title).setText(it)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(onboardingItem: OnboardingItem) =
            OnboardingFirstFragment().apply {
                arguments = OnboardingFirstFragmentArgs(onboardingItem).toBundle()
            }
    }
}