package com.nicolasmilliard.socialcats.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.nicolasmilliard.socialcats.model.User
import com.nicolasmilliard.socialcats.search.ui.R

internal class UserResultAdapter(
    private val inflater: LayoutInflater,
    private val callback: Callback
) : ListAdapter<UserResult, UserResultViewHolder>(UserResultItemCallback) {

    fun invokeFirstItem() {
        currentList.firstOrNull()?.let { callback.onUserClicked(it.user) }
    }

    override fun onBindViewHolder(holder: UserResultViewHolder, position: Int) {
        holder.update(currentList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserResultViewHolder {
        val view = inflater.inflate(R.layout.user, parent, false)
        return UserResultViewHolder(view, callback)
    }

    interface Callback {
        fun onUserClicked(user: User)
    }

    private object UserResultItemCallback : ItemCallback<UserResult>() {
        override fun areItemsTheSame(oldItem: UserResult, newItem: UserResult): Boolean {
            return oldItem.user.id == newItem.user.id
        }

        override fun areContentsTheSame(oldItem: UserResult, newItem: UserResult): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: UserResult, newItem: UserResult): Any? {
            if (oldItem == newItem) {
                return null
            }
            return Unit // Dummy value to prevent item change animation.
        }
    }
}
