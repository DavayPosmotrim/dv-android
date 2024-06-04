package com.davai.uikit_sample

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.davai.uikit.extensions.applyBlurEffect
import com.davai.uikit.extensions.clearBlurEffect

class CustomDialog : DialogFragment() {

    private var title: String? = "Заголовок диалога"
    private var message: String? = "Текст диалога"
    private var yesAction: (() -> Unit)? = null
    private var noAction: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(com.davai.uikit.R.drawable.session_card_background)
        val view = inflater.inflate(com.davai.uikit.R.layout.layout_custom_dialog, container, false)
        activity?.window?.decorView?.applyBlurEffect()

        val titleTextView = view.findViewById<TextView>(com.davai.uikit.R.id.tv_dialog_title)
        val messageTextView = view.findViewById<TextView>(com.davai.uikit.R.id.tv_dialog_message)
        val btnYes = view.findViewById<Button>(com.davai.uikit.R.id.btn_yes)
        val btnNo = view.findViewById<Button>(com.davai.uikit.R.id.btn_no)

        titleTextView.text = title
        messageTextView.text = message

        btnYes.setOnClickListener {
            yesAction?.invoke()
            dialog?.dismiss()
            activity?.window?.decorView?.clearBlurEffect()
        }

        btnNo.setOnClickListener {
            noAction?.invoke()
            dialog?.dismiss()
            activity?.window?.decorView?.clearBlurEffect()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * SCREEN_WIDTH).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        activity?.window?.decorView?.clearBlurEffect()
        noAction?.invoke()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity?.window?.decorView?.clearBlurEffect()
    }

    companion object {
        private const val SCREEN_WIDTH = 0.9

        fun newInstance(
            title: String,
            message: String,
            yesAction: (() -> Unit)? = null,
            noAction: (() -> Unit)? = null
        ): CustomDialog {
            val dialog = CustomDialog()
            dialog.title = title
            dialog.message = message
            dialog.yesAction = yesAction
            dialog.noAction = noAction
            return dialog
        }
    }
}
