package com.lookie.instadownloader.ui.download

import com.lookie.instadownloader.data.room.entity.Post
import java.text.SimpleDateFormat
import java.util.Locale

class PostViewModel(post: Post) {
  var id = post.id
  var shortcode = post.shortcode
  var displayUrl = post.displayUrl
  var videoUrl = post.videoUrl
  var isVideo = post.isVideo
  var children = post.children
  var caption = post.caption
  var owner = post.owner
  var text = post.text
  var createAt = post.createAt
  var isMultiMedia = post.children.edges!!.isNotEmpty()

  companion object {
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
  }
}