package com.davai.uikit_sample

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.davai.uikit_sample.databinding.ActivityProgressbarViewExampleBinding

class ProgressBarViewExample : AppCompatActivity() {

    private val binding: ActivityProgressbarViewExampleBinding by lazy {
        ActivityProgressbarViewExampleBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        showProgressBar(true)
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.dimView.visibility = if (show) View.VISIBLE else View.GONE
    }
}