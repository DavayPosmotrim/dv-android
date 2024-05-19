package com.davay.android.feature.createsession.presentation

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentCompilationsBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.createsession.di.DaggerCreateSessionFragmentComponent

class CompilationsFragment : BaseFragment<FragmentCompilationsBinding, CompilationsViewModel>(
    FragmentCompilationsBinding::inflate
) {
    override val viewModel: CompilationsViewModel by injectViewModel<CompilationsViewModel>()
    private var compilationAdapter: CompilationsAdapter? = null

    override fun diComponent(): ScreenComponent = DaggerCreateSessionFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent()).build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        compilationAdapter = CompilationsAdapter {
            viewModel.compilationClicked(it)
        }
        binding.rvCompilations.adapter = compilationAdapter
        binding.rvCompilations.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvCompilations.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
                .apply {
                    setDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            com.davai.uikit.R.drawable.divider,
                            requireContext().theme
                        )!!
                    )
                }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compilationAdapter = null
    }

    companion object {
        fun newInstance() = CompilationsFragment()
    }
}
