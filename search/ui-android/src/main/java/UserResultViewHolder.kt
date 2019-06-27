package com.nicolasmilliard.socialcats.search

import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nicolasmilliard.socialcats.model.User
import com.nicolasmilliard.socialcats.search.ui.R

internal class UserResultViewHolder(
    root: View,
    private val callback: UserResultAdapter.Callback
) : RecyclerView.ViewHolder(root), OnClickListener {
    private val userAdditionalText: TextView = root.findViewById(R.id.user_additional)
    private val userNameText: TextView = root.findViewById(R.id.user_name)

    init {
        root.setOnClickListener(this)
    }

    private var user: User? = null

    override fun onClick(view: View) {
        callback.onUserClicked(user!!)
    }

    fun update(userResult: UserResult) {
        val user = userResult.user
        this.user = user

        userNameText.text = user.name
        userAdditionalText.text = user.id
    }
}
