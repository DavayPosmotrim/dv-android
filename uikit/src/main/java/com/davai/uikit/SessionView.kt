package com.davai.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import coil.load
import coil.transform.RoundedCornersTransformation

class SessionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var tvDate: TextView? = null
    private var tvCoincidences: TextView? = null
    private var tvNamesList: TextView? = null
    private var ivCover: ImageView? = null

    init {
        initViews()
    }

    private fun initViews() {
        LayoutInflater.from(context).inflate(R.layout.session_view, this, true)
        tvDate = findViewById(R.id.tv_session_date)
        tvCoincidences = findViewById(R.id.tv_session_coincidences)
        tvNamesList = findViewById(R.id.tv_session_names_list)
        ivCover = findViewById(R.id.iv_session_cover)
    }

    fun setDate(date: String) {
        tvDate?.text = date
    }

    fun setCoincidences(amount: Int) {
        tvCoincidences?.text =
            String.format(resources.getString(R.string.session_coincidences), amount.toString())
    }

    fun setNamesList(names: String) {
        tvNamesList?.text = names
    }

    fun setCover(url: String) {
        ivCover?.load(url) {
            error(R.drawable.placeholder_theme_112)
                .scale(coil.size.Scale.FIT)
            placeholder(R.drawable.placeholder_theme_112)
                .scale(coil.size.Scale.FIT)

            transformations(
                RoundedCornersTransformation()
            )
        }
    }
}