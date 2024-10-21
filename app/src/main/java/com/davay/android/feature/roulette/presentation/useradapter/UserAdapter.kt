package com.davay.android.feature.roulette.presentation.useradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davay.android.databinding.ItemParticipantsBinding
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel

class UserAdapter : RecyclerView.Adapter<UserViewHolder>() {

    private val itemList: MutableList<UserRouletteModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserViewHolder(
            ItemParticipantsBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.count()

    fun setItems(items: List<UserRouletteModel>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(index: Int, item: UserRouletteModel) {
        itemList[index].isConnected = item.isConnected
        notifyItemChanged(index, item)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (val latestPayload = payloads.lastOrNull()) {
            is UserRouletteModel -> holder.bindStyle(latestPayload)
            else -> onBindViewHolder(holder, position)
        }
    }
}