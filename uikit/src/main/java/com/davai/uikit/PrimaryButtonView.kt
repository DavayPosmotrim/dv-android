package com.davai.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView

class PrimaryButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var buttonText: String
    private var buttonEnabled: Boolean
    private var buttonLoading: Boolean
    private val frame: View
    private val textView: TextView
    private val progressBar: ProgressBar

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.PrimaryButtonView,
            defStyleAttr,
            defStyleRes
        )
        buttonText = typedArray.getString(R.styleable.PrimaryButtonView_button_text) ?: ""
        buttonEnabled = typedArray.getBoolean(R.styleable.PrimaryButtonView_button_enabled, true)
        buttonLoading = typedArray.getBoolean(R.styleable.PrimaryButtonView_button_loading, false)
        typedArray.recycle()
        LayoutInflater.from(context).inflate(R.layout.primary_button_view, this)
        frame = findViewById<View>(R.id.frame)
        textView = findViewById<TextView>(R.id.text_view)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        setButtonText(buttonText)
        setButtonEnabled(buttonEnabled)
        setLoading(buttonLoading)
        textView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                frame.visibility = View.VISIBLE
            } else {
                frame.visibility = View.INVISIBLE
            }
        }
    }

    fun setButtonText(text: String) {
        buttonText = text
        if (progressBar.visibility != View.VISIBLE){
            textView.text = text
        }
    }

    fun setButtonEnabled(isEnabled: Boolean) {
        textView.isEnabled = isEnabled
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            textView.text = ""
            progressBar.visibility = View.VISIBLE
        } else {
            textView.text = buttonText
            progressBar.visibility = View.GONE
        }
    }
}