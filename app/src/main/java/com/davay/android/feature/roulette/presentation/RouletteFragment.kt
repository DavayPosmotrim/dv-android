package com.davay.android.feature.roulette.presentation

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import com.davay.android.app.AppComponentHolder
import com.davay.android.base.BaseFragment
import com.davay.android.databinding.FragmentRouletteBinding
import com.davay.android.di.ScreenComponent
import com.davay.android.feature.roulette.di.DaggerRouletteFragmentComponent
import com.davay.android.feature.roulette.presentation.model.FilmRouletteModel
import com.davay.android.feature.roulette.presentation.recycler.BoundsOffsetDecoration
import com.davay.android.feature.roulette.presentation.recycler.CarouselAdapter
import com.davay.android.feature.roulette.presentation.recycler.CarouselLayoutManager
import com.davay.android.feature.roulette.presentation.recycler.LinearHorizontalSpacingDecoration

class RouletteFragment :
    BaseFragment<FragmentRouletteBinding, RouletteViewModel>(FragmentRouletteBinding::inflate) {

    override val viewModel: RouletteViewModel by injectViewModel<RouletteViewModel>()
    private val adapter: CarouselAdapter = CarouselAdapter(
        listOf(
            FilmRouletteModel(
                id = 1,
                title = "Название 1",
                mark = "5.0",
                originalTitle = "gfdgd",
                yearCountryRuntime = "2008, USA",
                posterUrl = "https://atthemovies.uk/cdn/shop/files/front_07977eff-c3c7-4aa6-a57d-5d3e34727031.png?v=1717112425"
            ),
            FilmRouletteModel(
                id = 2,
                title = "Название 2",
                mark = "7.0",
                originalTitle = "gfdgd",
                yearCountryRuntime = "2008, USA",
                posterUrl = "https://atthemovies.uk/cdn/shop/files/back_ff1004f8-11e7-4e2b-81de-a9cd2a3de2c0.png?v=1717112172"
            ),
            FilmRouletteModel(
                id = 3,
                title = "Название 3",
                mark = "9.4",
                originalTitle = "gfdgd",
                yearCountryRuntime = "1991, UK",
                posterUrl = "https://atthemovies.uk/cdn/shop/products/Gladiator2000us27x40in195u.jpg?v=1621385091"
            ),
            FilmRouletteModel(
                id = 4,
                title = "Название 4",
                mark = "5.0",
                originalTitle = "gfdgd",
                yearCountryRuntime = "2018, USA",
                posterUrl = "https://atthemovies.uk/cdn/shop/files/front_efd356d9-ab5b-4dc3-a37f-452027a2b68d.png?v=1717110841"
            ),
            FilmRouletteModel(
                id = 5,
                title = "Название 5",
                mark = "3.0",
                originalTitle = "gfdgd",
                yearCountryRuntime = "2010, Canada",
                posterUrl = "https://atthemovies.uk/cdn/shop/products/Template_cb39750d-7a4b-44ec-950d-172f4c2fe96c.jpg?v=1666078856"
            ),
        )
    )

    override fun diComponent(): ScreenComponent = DaggerRouletteFragmentComponent.builder()
        .appComponent(AppComponentHolder.getComponent())
        .build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager =
            CarouselLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        val spacing = resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_24)
        binding.recyclerView.addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
        binding.recyclerView.addItemDecoration(BoundsOffsetDecoration())
        LinearSnapHelper().attachToRecyclerView(binding.recyclerView)
    }
}
