package com.lookie.instadownloader.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lookie.instadownloader.R
import com.lookie.instadownloader.data.room.entity.User
import com.lookie.instadownloader.databinding.ListItemUserBinding

class UserAdapter internal constructor(private val listener: OnItemClickListener) :
  ListAdapter<User, UserAdapter.ViewHolder>(PostDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      listener, DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.list_item_user, parent, false
      )
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position), position)
  }

  class ViewHolder(
    private val listener: OnItemClickListener,
    private val binding: ListItemUserBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, position: Int) {

      with(binding) {
        viewModel = UserViewModel(user)
        executePendingBindings()
      }

      Glide.with(binding.imgMedia.context)
        .load(user.profilePicUrl)
        .thumbnail(0.2f)
        .into(binding.imgMedia)

      binding.layoutMedia.setOnClickListener { view ->
        listener.onItemClick(view, user, position)
      }
    }
  }

  interface OnItemClickListener {
    fun onItemClick(view: View?, user: User?, position: Int)
  }
}

private class PostDiffCallback : DiffUtil.ItemCallback<User>() {

  override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
    return oldItem == newItem
  }
}