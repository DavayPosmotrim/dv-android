package com.davay.android.core.data.converters

import com.davay.android.core.data.database.entity.MovieDetailsEntity
import com.davay.android.core.data.database.entity.SessionEntity
import com.davay.android.core.data.database.entity.SessionWithMoviesDb
import com.davay.android.core.data.dto.CollectionDto
import com.davay.android.core.data.dto.GenreDto
import com.davay.android.core.data.dto.MovieDetailsDto
import com.davay.android.core.data.dto.MovieDto
import com.davay.android.core.data.dto.SessionDto
import com.davay.android.core.data.dto.SessionStatusDto
import com.davay.android.core.data.dto.UserDto
import com.davay.android.core.domain.models.CompilationFilms
import com.davay.android.core.domain.models.Genre
import com.davay.android.core.domain.models.Movie
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.SessionWithMovies
import com.davay.android.core.domain.models.User
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun CollectionDto.toDomain() = CompilationFilms(
    id,
    name,
    imgUrl
)

fun GenreDto.toDomain() = Genre(name)

fun MovieDto.toDomain() = Movie(
    id,
    name,
    imgUrl
)

fun UserDto.toDomain() = User(
    userId,
    name
)

fun MovieDetailsDto.toDomain() = MovieDetails(
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

/**
 * Конвертирует строку с датой в timeStamp.
 * Если при форматировании дата получается null, то возвращается дата minDate.
 * Если при форматировании дата больше текущей, то возвращается дата minDate.
 * Если при форматировании дата меньше minDate, то возвращается minDate.
 * minDate устанавливаем на 2024-01-01.
 */
fun SessionDto.toDomain(): Session {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val minDate = dateFormat.parse("2024-01-01")
    val currentDate = Date()

    val parsedDate = try {
        dateFormat.parse(date)
    } catch (e: ParseException) {
        null
    }

    val validDate = when {
        parsedDate == null -> minDate
        parsedDate.before(minDate) -> minDate
        parsedDate.after(currentDate) -> minDate
        else -> parsedDate
    }

    val timestamp = validDate.time

    return Session(
        id = id,
        users = users.map { it.name },
        numberOfMatchedMovies = numberOfMatchedMovies,
        date = timestamp,
        status = status.toDomain(),
        imgUrl = imgUrl
    )
}

fun SessionStatusDto.toDomain(): SessionStatus {
    return when (this) {
        SessionStatusDto.WAITING -> SessionStatus.WAITING
        SessionStatusDto.VOTING -> SessionStatus.VOTING
        SessionStatusDto.CLOSED -> SessionStatus.CLOSED
        SessionStatusDto.ROULETTE -> SessionStatus.ROULETTE
    }
}

fun MovieDetailsEntity.toDomain(): MovieDetails {
    return MovieDetails(
        id = movieId,
        name = name,
        description = description,
        year = year,
        countries = countries?.toListData(),
        imgUrl = imgUrl,
        alternativeName = alternativeName,
        ratingKinopoisk = ratingKinopoisk,
        ratingImdb = ratingImdb,
        numOfMarksKinopoisk = numOfMarksKinopoisk,
        numOfMarksImdb = numOfMarksImdb,
        duration = duration,
        genres = genres.toListData(),
        actors = actors?.toListData(),
        directors = directors?.toListData()
    )
}

fun SessionEntity.toDomain(): Session {
    return Session(
        id = sessionId,
        users = users.toListData(),
        numberOfMatchedMovies = numberOfMatchedMovies,
        date = date,
        status = SessionStatus.CLOSED,
        imgUrl = imgUrl
    )
}

fun MovieDetails.toDbEntity(): MovieDetailsEntity {
    return MovieDetailsEntity(
        movieId = id,
        name = name,
        description = description,
        year = year,
        countries = countries?.toStringData(),
        imgUrl = imgUrl,
        alternativeName = alternativeName,
        ratingKinopoisk = ratingKinopoisk,
        ratingImdb = ratingImdb,
        numOfMarksKinopoisk = numOfMarksKinopoisk,
        numOfMarksImdb = numOfMarksImdb,
        duration = duration,
        genres = genres.toStringData(),
        actors = actors?.toStringData(),
        directors = directors?.toStringData(),
    )
}

fun Session.toDbEntity(): SessionEntity {
    return SessionEntity(
        sessionId = id,
        users = users.map { it }.toStringData(),
        numberOfMatchedMovies = numberOfMatchedMovies ?: 0,
        date = date,
        imgUrl = imgUrl
    )
}

fun List<String>.toStringData(): String {
    return joinToString(separator = ";")
}

fun String.toListData(): List<String> {
    return split(";")
}

fun SessionWithMoviesDb.toDomain(): SessionWithMovies {
    return SessionWithMovies(
        session = session.toDomain(),
        movies = movies.map { it.toDomain() }
    )
}
