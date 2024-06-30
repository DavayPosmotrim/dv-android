package com.davai.uikit_sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.davai.uikit.MainDialogFragment
import com.davai.uikit.extensions.applyBlurEffect
import com.davai.uikit_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var dialog: MainDialogFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        clickListeners()
        binding.toBlurExample.applyBlurEffect(2f)
    }

    private fun clickListeners() = with(binding) {
        listOf(
            btnToShowProgressBar,
            btnToMoviewEvalution,
            btnToDvBanner,
            btnToDvButton,
            btnToDvFilm,
            btnToDvSession,
            btnToDvMovieSelection,
            btnToDvToolbar,
            toMsb,
            toBlurExample,
            btnToDvTags,
            btnToCustomDialog
        ).forEach {
            it.setOnClickListener(onClickListener())
        }
    }

    private fun onClickListener() = View.OnClickListener {
        with(binding) {
            when (it) {
                btnToShowProgressBar -> startActivity(createIntent(ProgressBarViewExample::class.java))
                btnToMoviewEvalution -> startActivity(createIntent(MovieEvaluationExample::class.java))
                btnToDvBanner -> startActivity(createIntent(BannerViewExample::class.java))
                btnToDvButton -> startActivity(createIntent(ButtonViewExampleActivity::class.java))
                btnToDvFilm -> startActivity(createIntent(MoovieCardViewExampleActivity::class.java))
                btnToDvSession -> startActivity(createIntent(SessionExample::class.java))
                btnToDvToolbar -> startActivity(createIntent(ToolbarExampleActivity::class.java))
                btnToDvMovieSelection -> startActivity(createIntent(MovieSelectionExampleActivity::class.java))
                toMsb -> startActivity(createIntent(MainScreenButtonViewExample::class.java))
                btnToDvTags -> startActivity(createIntent(TagViewExample::class.java))
                toBlurExample -> startActivity(createIntent(BlurActivity::class.java))
                btnToCustomDialog -> showCustomDialog()
            }
        }
    }

    private fun <T> createIntent(activityClass: Class<T>): Intent {
        return Intent(this@MainActivity, activityClass)
    }

    private fun showCustomDialog() {
        dialog = MainDialogFragment.newInstance(
            title = getString(R.string.dialog_title),
            message = getString(R.string.dialog_message),
            yesAction = {
                dialog?.dismiss()
            },
            noAction = {
                dialog?.dismiss()
            }
        )
        dialog?.show(supportFragmentManager, "CustomDialog")
    }
}

