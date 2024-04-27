package com.davai.uikit_sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.davai.uikit_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        clickListeners()
    }

    private fun clickListeners() = with(binding) {
        listOf(
            btnToMoviewEvalution,
            btnToDvBanner,
            btnToDvButton,
            btnToDvFilm,
            btnToDvSession,
            btnToDvMovieSelection,
            btnToDvToolbar
        ).forEach {
            it.setOnClickListener(onClickListener())
        }
    }

    private fun onClickListener() = View.OnClickListener {
        with(binding) {
            when (it) {
                btnToMoviewEvalution -> Toast.makeText(
                    this@MainActivity,
                    "To Movie Evaluation",
                    Toast.LENGTH_SHORT
                ).show()

                btnToDvBanner -> Toast.makeText(
                    this@MainActivity,
                    "ToDvBanner",
                    Toast.LENGTH_SHORT
                )
                    .show()

                btnToDvButton -> Toast.makeText(
                    this@MainActivity,
                    "ToDvButton",
                    Toast.LENGTH_SHORT
                )
                    .show()

                btnToDvFilm -> Toast.makeText(
                    this@MainActivity,
                    "ToDvFilm",
                    Toast.LENGTH_SHORT
                )
                    .show()

                btnToDvSession -> Toast.makeText(
                    this@MainActivity,
                    "ToDvSession",
                    Toast.LENGTH_SHORT
                ).show()

                btnToDvToolbar -> Toast.makeText(
                    this@MainActivity,
                    "ToDvToolbar",
                    Toast.LENGTH_SHORT
                ).show()

                btnToDvMovieSelection -> Toast.makeText(
                    this@MainActivity,
                    "ToDvMovieSelection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}