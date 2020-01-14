package com.lookie.instadownloader.ui.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.lookie.instadownloader.data.remote.model.ShortMediaModel
import com.lookie.instadownloader.data.room.entity.Post
import com.lookie.instadownloader.data.room.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostListViewModel(private val postRepository: PostRepository) : ViewModel() {

  var postsLiveData: LiveData<PagedList<Post>>

  private val pageSize = 10

  init {
    val factory: DataSource.Factory<Int, Post> = postRepository.getAllPosts()
    val pagedListBuilder: LivePagedListBuilder<Int, Post> =
      LivePagedListBuilder<Int, Post>(factory, pageSize)
    postsLiveData = pagedListBuilder.build()
  }

  fun getPostLiveData() = postsLiveData

  val posts: LiveData<List<Post>> = postRepository.getPosts()

  //CoroutineContext:
  private val completableJob = Job()
  private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)


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