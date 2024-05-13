package com.davay.android.feature.load.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davay.android.R
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentLoadBinding

class LoadFragment : BaseFragment<FragmentLoadBinding, LoadViewModel>(
    FragmentLoadBinding::inflate
) {

    override val viewModel: LoadViewModel by injectViewModel<LoadViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener { _ ->
            viewModel.navigate(R.id.action_loadFragment_to_mainFragment)
        }
    }

}