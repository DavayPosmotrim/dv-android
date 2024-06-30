package com.davay.android.feature.roulette.presentation.useradapter

import androidx.recyclerview.widget.RecyclerView
import com.davai.uikit.TagView
import com.davay.android.databinding.ItemParticipantsBinding
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel

class UserViewHolder(
    private val binding: ItemParticipantsBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: UserRouletteModel) = with(binding.root) {
        setText(user.name)
        if (user.isConnected) {
            changeStyle(TagView.Companion.Style.SECONDARY_GREEN)
        } else {
            changeStyle(TagView.Companion.Style.SECONDARY_GRAY)
        }
    }

    fun bindStyle(user: UserRouletteModel) = with(binding.root) {
        if (user.isConnected) {
            changeStyle(TagView.Companion.Style.SECONDARY_GREEN)
        } else {
            changeStyle(TagView.Companion.Style.SECONDARY_GRAY)
        }
    }
}
