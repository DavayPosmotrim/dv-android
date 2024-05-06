package com.davai.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView

class SecondaryButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var buttonText: String
    private var buttonEnabled: Boolean
    private var buttonLoading: Boolean
    private val textView: TextView
    private val progressBar: ProgressBar

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SecondaryButtonView,
            defStyleAttr,
            defStyleRes
        )
        buttonText = typedArray.getString(R.styleable.SecondaryButtonView_button_text_sec) ?: ""
        buttonEnabled =
            typedArray.getBoolean(R.styleable.SecondaryButtonView_button_enabled_sec, true)
        buttonLoading =
            typedArray.getBoolean(R.styleable.SecondaryButtonView_button_loading_sec, false)
        typedArray.recycle()
        LayoutInflater.from(context).inflate(R.layout.secondary_button_view, this)
        textView = findViewById<TextView>(R.id.text_view)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        setButtonText(buttonText)
        setButtonEnabled(buttonEnabled)
        setLoading(buttonLoading)
    }

    fun setButtonText(text: String) {
        buttonText = text
        if (progressBar.visibility != View.VISIBLE && textView.isEnabled) {
            textView.text = text
        }
    }

    fun setButtonEnabled(isEnabled: Boolean) {
        buttonEnabled = isEnabled
        textView.isEnabled = isEnabled
        if (textView.isEnabled) {
            textView.text = buttonText
        } else {
            textView.text = ""
        }
    }

    fun setLoading(loading: Boolean) {
        buttonLoading = loading
        if (loading) {
            textView.text = ""
            progressBar.visibility = View.VISIBLE
        } else {
            textView.text = buttonText
            progressBar.visibility = View.GONE
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return when (ev?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                return buttonEnabled && !buttonLoading
            }

            else -> {
                false
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                textView.setTextColor(resources.getColor(R.color.text_caption_dark, context.theme))
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                textView.setTextColor(resources.getColor(R.color.text_base, context.theme))
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}