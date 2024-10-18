package com.davay.android.core.data.converters

import com.davay.android.core.data.database.entity.MovieDetailsEntity
import com.davay.android.core.data.database.entity.MovieIdEntity
import com.davay.android.core.data.database.entity.SessionEntity
import com.davay.android.core.data.database.entity.SessionWithMoviesDb
import com.davay.android.core.data.dto.CollectionDto
import com.davay.android.core.data.dto.GenreDto
import com.davay.android.core.data.dto.MovieDetailsDto
import com.davay.android.core.data.dto.MovieDto
import com.davay.android.core.data.dto.SessionDto
import com.davay.android.core.data.dto.SessionResultDto
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
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

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
    userId = userId,
    name = name
)

private const val RATING_MULTIPLIER = 10
private const val ROUNDING_DIVIDER = 10

fun MovieDetailsDto.toDomain() = MovieDetails(
    id,
    name,
    description,
    year,
    countries,
    imgUrl?.let {
        URLDecoder.decode(it.removePrefix("/"), StandardCharsets.UTF_8.toString())
    } ?: "",
    alternativeName,
    roundRating(ratingKinopoisk),
    roundRating(ratingImdb),
    numOfMarksKinopoisk,
    numOfMarksImdb,
    duration,
    genres.map { it.name },
    personsArrFormatter(directors),
    personsArrFormatter(actors),
)

private fun roundRating(rating: Float) = round(rating * RATING_MULTIPLIER) / ROUNDING_DIVIDER

private fun personsArrFormatter(strList: List<String?>?): List<String> {
    return strList?.mapNotNull {
        if (it.isNullOrEmpty()) {
            null
        } else {
            it
        }
    } ?: emptyList()
}


fun SessionDto.toDomain() = Session(
    id = id,
    users = users.map { it },
    movieIdList = movieIdList,
    matchedMovieIdList = matchedMovieIdList,
    date = convertDateStringToTimestamp(date),
    status = status.toDomain(),
    imgUrl = imgUrl ?: ""
)

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
        directors = directors?.toListData(),
        actors = actors?.toListData(),
    )
}

fun SessionEntity.toDomain(): Session {
    return Session(
        id = sessionId,
        users = users.toListData(),
        movieIdList = emptyList(),
        matchedMovieIdList = List(numberOfMatchedMovies) { it },
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
        numberOfMatchedMovies = matchedMovieIdList.size,
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

fun MovieIdEntity.toDomain(): Int = this.movieId

fun SessionResultDto.toDomain() = Session(
    id = id,
    users = users.map { it.name },
    movieIdList = emptyList(),
    matchedMovieIdList = matchedMovies.map { it.id },
    date = convertDateStringToTimestamp(date),
    status = SessionStatus.CLOSED,
    imgUrl = imgUrl?.let {
        URLDecoder.decode(it.removePrefix("/"), StandardCharsets.UTF_8.toString())
    } ?: ""
)

/**
 * Конвертирует строку с датой в timeStamp.
 * Если при форматировании дата получается null, то возвращается дата minDate.
 * Если при форматировании дата больше текущей, то возвращается дата minDate.
 * Если при форматировании дата меньше minDate, то возвращается minDate.
 * minDate устанавливаем на 2024-01-01.
 */
private fun convertDateStringToTimestamp(dateString: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val minDate = dateFormat.parse("2024-01-01")
    val currentDate = Date()
    val parsedDate = try {
        dateFormat.parse(dateString)
    } catch (e: ParseException) {
        null
    }
    val validDate = when {
        parsedDate == null -> minDate
        parsedDate.before(minDate) -> minDate
        parsedDate.after(currentDate) -> minDate
        else -> parsedDate
    }
    return validDate.time
}
