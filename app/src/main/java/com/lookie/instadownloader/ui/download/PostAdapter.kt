package com.lookie.instadownloader.ui.download

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdRequest
import com.lookie.instadownloader.R
import com.lookie.instadownloader.data.room.entity.Post
import com.lookie.instadownloader.databinding.ListItemPostBinding

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
    val post = getItem(position)
    if (post != null) {
      holder.bind(post, position)
    }
  }

  class ViewHolder(
    private val listener: OnItemClickListener,
    private val binding: ListItemPostBinding
  ) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post, position: Int) {

      with(binding) {
        viewModel = PostViewModel(post)
        executePendingBindings()
      }

      if (!post.owner.profilePicUrl.isNullOrEmpty()) {
        Glide.with(binding.imgAvatar.context)
          .load(post.owner.profilePicUrl)
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(binding.imgAvatar)
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
    return oldItem == newItem
  }
}