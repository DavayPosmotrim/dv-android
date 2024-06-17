package com.davay.android.feature.lastsession.presentation.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davai.uikit.TagView

class UserViewHolder(
    private val tag: TagView
) : RecyclerView.ViewHolder(tag) {
    fun bind(user: String) {
        tag.setText(user)
    }

    companion object {
        fun from(parent: ViewGroup): UserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(com.davai.uikit.R.layout.tag_item, parent, false) as TagView
            return UserViewHolder(view)
        }
    }
}
