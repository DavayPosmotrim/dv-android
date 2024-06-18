package com.davay.android.feature.lastsession.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.davai.extensions.dpToPx
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentLastSessionBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.lastsession.di.DaggerLastSessionFragmentComponent
import com.davay.android.feature.lastsession.presentation.adapters.FilmAdapter
import com.davay.android.feature.sessionlist.presentation.adapter.CustomItemDecorator
import com.davay.android.feature.sessionlist.presentation.adapter.UserAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class LastSessionFragment : BaseFragment<FragmentLastSessionBinding, LastSessionViewModel>(
    FragmentLastSessionBinding::inflate
) {

    override val viewModel: LastSessionViewModel by injectViewModel<LastSessionViewModel>()
    private val userAdapter = UserAdapter()
    private val filmAdapter = FilmAdapter()
    override fun diComponent(): ScreenComponent =
        DaggerLastSessionFragmentComponent.builder().appComponent(AppComponentHolder.getComponent())
            .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(
            inflater,
            container,
            savedInstanceState
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

    }

    private fun initViews() {
        initUserRecycler()
        initFilmRecycler()
        userAdapter.setItems(listOf("Артем", "Руслан", "Константин", "Виктория", "Аристарх", "Коля"))
        filmAdapter.setItems(listOf("Film 1", "Film 2", "Film 3", "Film 4", "Film 5", "Film 6"))

    }


    private fun initUserRecycler() {
        val spaceBetweenItems = SPACING_BETWEEN_RV_ITEMS_8_DP.dpToPx()

        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }

        binding.rvUser.apply {
            adapter = userAdapter
            layoutManager = flexboxLayoutManager
            addItemDecoration(CustomItemDecorator(spaceBetweenItems))
        }
    }

    private fun initFilmRecycler() {

        binding.rvFilm.apply {
            adapter = filmAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }


    companion object {
        private const val SPACING_BETWEEN_RV_ITEMS_8_DP = 8
    }


}