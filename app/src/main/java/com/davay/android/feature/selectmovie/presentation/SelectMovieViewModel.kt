package com.davay.android.feature.selectmovie.presentation

import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.MovieDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SelectMovieViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow<List<MovieDetails>>(emptyList())
    val state = _state.asStateFlow()

    init {
        getFilms()
    }

    @Suppress(
        "Detekt.MaxLineLength",
        "Detekt.StringLiteralDuplication",
        "Detekt.ArgumentListWrapping",
        "Detekt.Wrapping",
        "Detekt.UnderscoresInNumericLiterals",
        "Detekt.LongMethod"
    )
    private fun getFilms() {
        val movies = listOf(
            MovieDetails(
                id = 12345,
                name = "Интерстеллар",
                description = "Когда засуха, пыльные бури и вымирание растений приводят человечество к продовольственному кризису, коллектив исследователей и учёных отправляется сквозь червоточину (которая предположительно соединяет области пространства-времени через большое расстояние) в путешествие, чтобы превзойти прежние ограничения для космических путешествий человека и найти планету с подходящими для человечества условиями.",
                year = "2015",
                countries = listOf(),
                imgUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/7ae40b66-ebda-4570-8676-980344d81bc1/3840x",
                alternativeName = "Interstellar",
                ratingKinopoisk = 8.6f,
                ratingImdb = 9.6f,
                numOfMarksKinopoisk = 112321,
                numOfMarksImdb = 4532142,
                duration = 169,
                genres = listOf("Фантастика", "Драма", "Приключения"),
                directors = listOf("Мэттью Макконахи", "Энн Хэтэуэй", "Джессика Честейн"),
                actors = listOf("Кристофер Нолан")
            ), MovieDetails(
                id = 8509,
                name = "Dorothea Tanner",
                description =
                "Бэтмен поднимает ставки в войне с криминалом. С помощью лейтенанта Джима Гордона и прокурора Харви Дента он намерен очистить улицы Готэма от преступности. Сотрудничество оказывается эффективным, но скоро они обнаружат себя посреди хаоса, развязанного восходящим криминальным гением, известным напуганным горожанам под именем Джокер.",
                year = "2008",
                countries = listOf(),
                imgUrl =
                "https://kinopoisk-ru.clstorage.net/30H4PS317/fc80630l6W4j/PCNvS_zGy0YMHp7no0AsGkQRD5F3GbGxVVkAfX4LQFrQ2bnuw6YEelehkDaD0ct6S5LEFcsAhqMFiXQJwV2qBmgDGrrdc46T6NorlwYFCJ6RcXtAE5yF8IRsBZiM-ELZ8k6bjTEkBrxDxbTX9mR-Hk-SVJGrlALXUqrFWuOaftAMACwryuRb55hPn2RQI3881LF8VVVJModFOsL-vWiyOGEOGexB9RSdkHY10iYmjX5vsIUCeWGTlCLLc5lj-jxzbAJMCDrFKIYITY200TA9PAVjD5VnKUDnBPjBv-9rYBuQ7wpOAUZGnZM1ZxJEVB6cbhJXMvgSQ2VCeVBo4bmfke9wTQoqNKs1690sBkJT_TyWsT2GlgiQByfLYE8923HqQF47HUNl5JxD9UdgdAfvvC7hlCIbE2PGYynw21I47zHu4O85yuVJtQot_DVxMg6c5fF8xRYJ0ceUS8Le7zsSScAdW98xpAbeIvZ3MldUbO89wjbR2gIAdvILQylzWW7DXTNuWuhVGmV4jF-kI5K9jzbAL9ek6GF1tzqzzM-LABjzLdk9kMZVjPJGtLGk9bwd7TE00ClCcJbQWbB5ghkPs30APJh5t0pFeN8-lXHQTX2mAx_nxysQ1XW60gytqVLYge8KHjCWVD3zNBVxlfR_vGwhtRIbg2EkoEuyOWBJjZMe8bz6CES59apcf4XxIs1cx2MddoRakLfUqXI93mly66MNCV4xB3TMc4VU0beUHJ-t0cbyGHJD5XBpIalRSZ8jPCB8q2qU-oV7r2wGIfJNfQaAbLSW-IPElUqDzS374UlDLTlNkPc1v7PlZNAk5Cx97hBUggowwcZhCaH5wdsuAG0gLop7Fwl2SX1-JcOTHz704Tx09yhh15R7cA-fuXIIoX3rzjHndkyQxaSRtHdujF7jJMGrQMGHYkthuPO7DhON4C4ZmoTJ95rdbCTTUsyetqM8VoYIYqe1qMHMnDviGPPPW-yxxhet0GWEwfZ2f9z8UYSCKKPi9SEoEAuQuNziTaO-GniWegVp_0x3YZFdb8WCPifkWbPFVZsCfc_LIWsiz_sMQBUkrmKnlpKElL3vzGJWkltw8QSCaXMIkjptMD7CTTmbtboXqr_v5wAyDJ2m4-7F1vqDxTWawx6veTI7sg4ZXlKUJGyTJHVxVKeubu3SVkLL0ZJmovuSKFPJX6P9k-9KSmTopFpMfEYgYz8s1XGsZ1d5YpcUatJvPjlx27H8WywDpJbPwaeVcyYFzZ7-YkQwWcLwZDGKMBsTmA8x_QFtKfl0qbfJnGylokK9LcRBH0WXidA21Wiyr_47AvuAPlteAicWnrBX5DJ1dq_fHDHEwklwY5dAegAIQTkuwVxAPlmrJCqF2d1OdQKSvywV4MyGtuhwRoTqgT79-iFJIt747XMVRp0yVodDZ-SeLkxQdWCpADGlA_khukNYT6MNkE8YO0ZJpBoPL6YRos9thWLdR6bpk2V0icH_Djjzq0AcOx8SNgT-EaXnM-f0zmxuIiZyaqBDNqO6UGkDm01j7iAcWeqk65cI3p3n8KIdP-SB3KQnifOl1MjQz_6ZoEsQvvm9QCdmjFH1lDKGdq4-rOJEQOoC0QeSGZD7IIruMGwyDVrptSnmSFyOVuKS7c71gNzGBdmx9dUbos_t-iJLQ14JP0P1NPxgF5VStKStzv4zNyKLY0NEEepAGOPJPDOOMd6LqPSIRZt97WWwYf0_9IAftKeKgtcVWJJcrVsCa5APOb1gN-b9sBZ18CQlnn7MckQyiLNgZWIqEhsQiS3B_BFOKts1GYUp3e50ImFvXtXi3YWnqsPktnjiLN1oMGuhDQkPEUfXb4D0pbE2N3w-z5LWEGoywVeBq1D64wmMAQ3w38pJNthkap_cZEJDbb0kwWz0pusghQbJ4i9ciwG5Y27Kb3DF5v4DtJVQNkQMHAzyNFJIM5NHI9oASvM7D5HPAz7Y6JZbNmp-LqfjYoyeRrPuBUZJs2f2isL8L_qQWKKNq_wjpNT8wTfXQ2WEDIxOcwZS60CxN3JrEHmAaGxgTnI82-ikCVaojj53oFKdfyQwXFalyPDntHowj_854plSnkvcI9fmvtJFVsIEl8x-7wPm46uBYZQQO6Ga8Uk-UVxRv-n4lgommg3ct4JhTT5lYy53V6ihtKX6oAz_iNAr800oD3Fk9v4iFmfyZdaenpwSdRCocALVkinh2KNaTMO8cQ4JWKY7FXleDpUwkC88JDEMBIbLctQlGQDNLfsQOKDs6c0AhpWu8EW10-d1T55OgddQucABFhOL0Dtzea1hjdAc6mpGi7a77z7H48INXpYA_gd1mpLG5ngwfP4q8huynmlewhQXHoIlRfKFt71t3dDWoOqw0reC-iH7gistwi3xHvlptFtVeE_MlhPCXv6Xk-4H9akyd5RLMY8MGoC78O-YHkK0Vm6RlFfQRJY9n46CZBLIovOmsiozGuAIHwANcw0ritbqRpgfLmfiUj38NWGeh6UZ0-Wk2yG_b8tBWmPtCQ9zNsS-0qZFk_dHbF4MIEZAGiDSVJA4c3rAOh9iH8Bf-AhmKxf5Xy1Xk8I__uVjfGclq8Lltalj3e46MpjyrhmewKZGvyOnpeJUV86Pz9C0QKtgoKbwCAOJIShOE_5TXNhpdPvmaWxMpWABPf2GA6yGlMmBtIapoEy_ihD443xaP1EHF3wzFNVQJKTcrz6B1iL6YMPWg2vCGTIqXmM_sc97iabpdpjP_efyEB6cN5Pe59f68hWlWwB_vdoiW5DfeBxSp0ZNAnSU4ZZ0H14tw-SCq6NCtFFqQviRSV5xr8ENKphESYZpbI1mQmHvTEYhzObFyqGndVuCHRw6o6nCfDovYBS0HJGGV4PH9oyeLsDU4bvzg1UTSnIY47jf451R_upI1um3eFwuhhESPw-V0CxFRSsg1RebUr9uatOIww3pbYClVlzyZgWBdiWdji8zl2GachHWs",
                alternativeName = "The Dark Knight",
                ratingKinopoisk = 8.5f,
                ratingImdb = 8.0f,
                numOfMarksKinopoisk = 50312,
                numOfMarksImdb = 231223,
                duration = 152,
                genres = listOf(
                    "Экшн",
                    "Криминал",
                    "Драма",
                    "Комиксы",
                    "Детектив",
                    "Мелодрама",
                    "Байопик"
                ),
                directors = listOf("Кристофер Нолан"),
                actors = listOf("Кристиан Бейл", "Хит Леджер", "Аарон Экхарт")
            ), MovieDetails(
                id = 34567,
                name = "Властелин колец: Возвращение короля",
                description = "Заключительная часть эпической трилогии.",
                year = "2003",
                countries = listOf("США", "Новая Зеландия", "Великобритания", "Австралия"),
                imgUrl = null,
                alternativeName = "The Lord of the Rings: The Return of the King",
                ratingKinopoisk = 6.7f,
                ratingImdb = 9.0f,
                numOfMarksKinopoisk = 52434,
                numOfMarksImdb = 231223,
                duration = null,
                genres = listOf("Фэнтези", "Приключения", "Драма"),
                directors = listOf("Питер Джексон", "Кристофер Нолан"),
                actors = listOf("Элайджа Вуд", "Вигго Мортенсен", "Иэн Маккеллен")
            ), MovieDetails(
                id = 3457,
                name = "Доктор Стрейнджлав, или Как я научился не волноваться и полюбил атомную бомбу",
                description = null,
                year = null,
                imgUrl =
                "https://avatars.mds.yandex.net/get-kinopoisk-image/1777765/cb430f00-1734-4078-abd2-6688a94749a5/3840x",
                countries = listOf("США", "Великобритания", "Австралия"),
                alternativeName = "Dr. Strangelove or: How I Learned to Stop Worrying and Love the Bomb",
                ratingKinopoisk = 6.7f,
                ratingImdb = 5.9f,
                numOfMarksKinopoisk = 433123,
                numOfMarksImdb = 5412322,
                duration = 201,
                genres = listOf("комедия", "фантастика", "триллер"),
                directors = listOf("Стэнли Кубрик", "Терри Саузерн", "Питер Джордж"),
                actors = listOf(
                    "Питер Селлерс",
                    "Джордж К. Скотт",
                    "Стерлинг Хейден",
                    "Кинен Уинн",
                )
            )
        )
        _state.value = movies
    }
}