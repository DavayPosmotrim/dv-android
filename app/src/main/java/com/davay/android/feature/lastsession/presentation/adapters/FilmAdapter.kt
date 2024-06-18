package com.davay.android.feature.lastsession.presentation.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FilmAdapter :
    RecyclerView.Adapter<FilmViewHolder>() {

    private val itemList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return FilmViewHolder.from(parent)
    }

    override fun getItemCount(): Int = itemList.count()

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    fun setItems(items: List<String>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    fun getItems(): List<String> = itemList

    fun clearItems() {
        itemList.clear()
        notifyDataSetChanged()
    }
}