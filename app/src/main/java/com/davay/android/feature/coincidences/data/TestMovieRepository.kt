package com.davay.android.feature.coincidences.data

import com.davay.android.base.usecases.GetData
import com.davay.android.domain.models.ErrorType
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import com.davay.android.utils.ConnectionChecker
import com.davay.android.utils.Result

class TestMovieRepository(
    private val connectionChecker: ConnectionChecker
) : GetData<MovieDetailsDemo, ErrorType> {

    override suspend fun getData(): Result<List<MovieDetailsDemo>, ErrorType> =
        if (connectionChecker.isConnected) {
            Result.Success(mockTestMovieList)
        } else {
            Result.Error(ErrorType.NO_CONNECTION)
        }

    companion object {
        private val urls = listOf(
            "https://images.squarespace-cdn.com/content/v1/57488e28746fb940f103c64e/" +
                "1626726014496-MF7OB9OBHLB21UR0LJV0/Fellowship+for+Trailer.jpg",
            "https://i.pinimg.com/originals/b8/79/e4/b879e41af44317004f6e765cb215b41c.jpg",
            "https://ruskino.ru/media/gallery/624/uxpmUNTVnFXMiVWIWSebd4l03TI.jpg",
            "https://i.pinimg.com/736x/5e/14/c7/5e14c743d3afd01c80f12e3a5171cada.jWind",
            "https://labande-annonce.fr/www-site/uploads/2014/12/affiche-whiplash-fr.jpg"
        )

        private val films = listOf(
            "Lord of the Rings" to urls[0],
            "Hateful Eight" to urls[1],
            "Солярис" to urls[2],
            "Nausicaa of the Valley of the Wind" to urls[3],
            "Whiplash" to urls[4]
        )

        private val mockTestMovieList = List(10) {
            MovieDetailsDemo(
                kinopoiskId = it,
                name = films[it % 5].first,
                posterUrl = films[it % 5].second
            )
        }
    }
}