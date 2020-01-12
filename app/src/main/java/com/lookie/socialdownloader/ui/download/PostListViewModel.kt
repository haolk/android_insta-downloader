package com.lookie.socialdownloader.ui.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lookie.socialdownloader.data.remote.model.ShortMediaModel
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.data.room.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostListViewModel(private val postRepository: PostRepository) : ViewModel() {

  //CoroutineContext:
  private val completableJob = Job()
  private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

  val posts: LiveData<List<Post>> = postRepository.getPosts()

  val lastPost: LiveData<Post> = postRepository.getLastPost()

  fun insertPost(media: ShortMediaModel?) {
    val post = Post(
      media!!.id!!,
      media.shortcode!!,
      media.displayUrl!!,
      media.videoUrl!!,
      media.isVideo,
      media.text!!,
      System.currentTimeMillis().toString(),
      media.children!!,
      media.caption!!,
      media.owner!!
    )
    coroutineScope.launch {
      if (!postRepository.isPosted(post.id)) {
        postRepository.insertPost(post)
      }
    }
  }

  fun deletePost(post: Post?) {
    coroutineScope.launch {
      postRepository.deletePost(post!!)
    }
  }
}