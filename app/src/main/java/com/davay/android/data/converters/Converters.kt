package com.davay.android.data.converters

import com.davay.android.data.dto.CollectionDto
import com.davay.android.data.dto.GenreDto
import com.davay.android.data.dto.MovieDetailsDto
import com.davay.android.data.dto.MovieDto
import com.davay.android.data.dto.SessionDto
import com.davay.android.data.dto.SessionStatusDto
import com.davay.android.data.dto.UserDto
import com.davay.android.domain.models.Collection
import com.davay.android.domain.models.Genre
import com.davay.android.domain.models.Movie
import com.davay.android.domain.models.MovieDetails
import com.davay.android.domain.models.Session
import com.davay.android.domain.models.SessionStatus
import com.davay.android.domain.models.User

fun CollectionDto.convert() = Collection(
    id,
    name,
    imgUrl
)

fun GenreDto.convert() = Genre(name)

fun MovieDto.convert() = Movie(
    id,
    name,
    imgUrl
)

fun UserDto.convert() = User(
    userId,
    name
)

fun MovieDetailsDto.convert() = MovieDetails(
    id,
    name,
    description,
    year,
    countries,
    imgUrl,
    alternativeName,
    ratingKinopoisk,
    ratingImdb,
    numOfMarksKinopoisk,
    numOfMarksImdb,
    duration,
    genres.map { it.name },
    actors,
    directors
)

fun SessionDto.convert() = Session(
    id,
    users.map { it.convert() },
    numberOfMatchedMovies,
    date,
    status.convert(),
    imgUrl
)

fun SessionStatusDto.convert(): SessionStatus {
    return when (this) {
        SessionStatusDto.WAITING -> SessionStatus.WAITING
        SessionStatusDto.VOTING -> SessionStatus.VOTING
        SessionStatusDto.CLOSED -> SessionStatus.CLOSED
    }
}