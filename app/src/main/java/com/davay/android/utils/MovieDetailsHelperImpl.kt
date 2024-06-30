package com.davay.android.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.davai.uikit.TagView
import com.davay.android.R
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import com.google.android.flexbox.FlexboxLayout

/**
 * Добавляем класс для избегания дублирования логики
 */
open class MovieDetailsHelperImpl : MovieDetailsHelper {
    override fun setImage(img: ImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            img.load(R.drawable.ic_movie_selection_error_332) {
                transformations(RoundedCornersTransformation())
                    .crossfade(true)
            }
        } else {
            img.load(url) {
                placeholder(com.davai.uikit.R.drawable.placeholder_general_80)
                    .scale(Scale.FIT)
                error(R.drawable.ic_movie_selection_error_332)
                    .scale(Scale.FIT)
                transformations(RoundedCornersTransformation())
                    .crossfade(true)
            }
        }
    }

    override fun setRateText(tvRate: TextView, ratingKinopoisk: Float?) {
        ratingKinopoisk?.let {
            val textColor = if (ratingKinopoisk >= GOOD_RATE_7) {
                tvRate.context.getColor(com.davai.uikit.R.color.done)
            } else {
                tvRate.context.getColor(com.davai.uikit.R.color.attention)
            }
            tvRate.apply {
                text = ratingKinopoisk.toString()
                setTextColor(textColor)
            }
        }
    }

    override fun addGenreList(flexBox: FlexboxLayout, genres: List<String>) {
        flexBox.removeAllViews()
        genres.forEach { genre ->
            val tagView = createTagView(genre, flexBox.context)
            flexBox.addView(tagView)
        }
    }

    override fun buildStringYearCountriesRuntime(data: MovieDetailsDemo, context: Context): String {
        with(data) {
            val str = StringBuilder()
            year?.let {
                str.append(it)
                str.append(DOT_DELIMETER)
            }
            if (countries.isNotEmpty()) {
                val countryList = countries.take(MAX_COUNTRY_NUMBER)
                val countriesString = countryList.joinToString(separator = DOT_DELIMETER)
                str.append(countriesString)
                if (countries.size > MAX_COUNTRY_NUMBER) {
                    str.append(MULTIPOINT)
                }
                str.append(DOT_DELIMETER)
            }
            movieLengthMin?.let {
                str.append(movieLengthMin.formatMovieDuration(context))
            }
            return str.toString()
        }
    }

    private fun Int.formatMovieDuration(context: Context): String {
        val hours = this / MINUTES_IN_HOUR
        val remainingMinutes = this % MINUTES_IN_HOUR

        return when {
            hours > 0 && remainingMinutes > 0 -> context.getString(
                R.string.select_movies_hours_and_minutes,
                hours,
                remainingMinutes
            )

            hours > 0 -> context.getString(R.string.select_movies_hours, hours)
            else -> context.getString(R.string.select_movies_minutes, remainingMinutes)
        }
    }

    private fun createTagView(text: String, context: Context): TagView {
        val tagView = TagView(context).apply {
            setText(text)
            changeStyle(TagView.Companion.Style.PRIMARY_VIOLET)
        }

        val layoutParams = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            val marginEnd =
                context.resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_8)
            val marginBottom =
                context.resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_12)
            setMargins(0, 0, marginEnd, marginBottom)
        }

        tagView.layoutParams = layoutParams
        return tagView
    }

    private companion object {
        const val MINUTES_IN_HOUR = 60
        const val DOT_DELIMETER = " ∙ "
        const val MULTIPOINT = "..."
        const val MAX_COUNTRY_NUMBER = 3
        const val GOOD_RATE_7 = 7.0f
    }
}
