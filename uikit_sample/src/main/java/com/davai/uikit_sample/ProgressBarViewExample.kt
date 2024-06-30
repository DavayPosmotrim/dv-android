package com.davai.uikit_sample

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.davai.uikit_sample.databinding.ActivityProgressbarViewExampleBinding

//class ProgressBarViewExample: AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_progressbar_view_example)
//        showProgressBar(true)
//    }
//
//    private fun showProgressBar(show: Boolean){
//        binding.ProgressBar.visibility = if (show) View.VISIBLE else View.GONE
//        binding.dimView.visibility = if (show) View.VISIBLE else View.GONE
//    }
//}
class ProgressBarViewExample : AppCompatActivity() {

    private lateinit var binding: ActivityProgressbarViewExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progressbar_view_example)
        binding = ActivityProgressbarViewExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProgressBar(true)
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.dimView.visibility = if (show) View.VISIBLE else View.GONE
    }
}