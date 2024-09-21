package com.davay.android.feature.sessionsmatched.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davai.util.setOnDebouncedClickListener
import com.davay.android.R
import com.davay.android.core.domain.models.Session
import com.davay.android.databinding.ItemSessionBinding
import com.davay.android.extensions.formatDate
import kotlinx.coroutines.CoroutineScope

class SessionListAdapter(
    private val coroutineScope: CoroutineScope,
    private val onSessionClickListener: ((session: Session) -> Unit)?
) : RecyclerView.Adapter<SessionListAdapter.SessionListViewHolder>() {

    private val sessionList = mutableListOf<Session>()

    class SessionListViewHolder(private val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Session) {
            val userList = session.users.toMutableList()
            userList[0] =
                binding.root.resources.getString(R.string.session_list_you_user, userList[0])
            val formatedDate = session.date.formatDate()
            binding.root.apply {
                setDate(formatedDate)
                setNamesList(
                    userList.joinToString(", ")
                )
                setCover(session.imgUrl)
                setCoincidences(session.matchedMovieIdList.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionListViewHolder {
        val binding = ItemSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = SessionListViewHolder(binding)
        viewHolder.itemView.setOnDebouncedClickListener(
            coroutineScope = coroutineScope
        ) { _ ->
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onSessionClickListener?.invoke(sessionList[position])
            }
        }
        return viewHolder
    }

    override fun getItemCount() = sessionList.size

    override fun onBindViewHolder(holder: SessionListViewHolder, position: Int) {
        val session = sessionList[position]
        holder.bind(session)
    }

    fun setData(sessions: List<Session>) {
        sessionList.clear()
        sessionList.addAll(sessions)
        notifyDataSetChanged()
    }
}