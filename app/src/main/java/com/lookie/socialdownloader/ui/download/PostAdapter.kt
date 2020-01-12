package com.lookie.socialdownloader.ui.download

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.ListItemPostBinding

class PostAdapter internal constructor(private val listener: OnItemClickListener) :
  ListAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      listener, DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.list_item_post, parent, false
      )
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position), position)
  }

  class ViewHolder(
    private val listener: OnItemClickListener,
    private val binding: ListItemPostBinding
  ) :
    RecyclerView.ViewHolder(binding.root) {
    init {

    }

    fun bind(post: Post, position: Int) {
      with(binding) {
        viewModel = PostViewModel(post)
        executePendingBindings()
      }

      binding.cardMedia.setOnClickListener { view ->
        listener.onItemClick(view, post, position)
      }

      binding.imgMenu.setOnClickListener { view ->
        listener.onItemClick(view, post, position)
      }
    }
  }

  interface OnItemClickListener {
    fun onItemClick(view: View?, post: Post?, position: Int)
  }
}

private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {

  override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
    return oldItem.shortcode == newItem.shortcode
  }

  override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
    return oldItem.shortcode == newItem.shortcode
  }
}