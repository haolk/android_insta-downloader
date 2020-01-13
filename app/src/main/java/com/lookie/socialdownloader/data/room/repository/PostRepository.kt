package com.lookie.socialdownloader.data.room.repository

import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.data.room.dao.PostDao

/**
 * @author Phung Nguyen on 2020-01-11 22:05
 **/
class PostRepository private constructor(private val postDao: PostDao) {

  fun getPosts() = postDao.getPosts()

  fun getLastPost() = postDao.getLastPost()

  fun getAllPosts() = postDao.getAllPosts()

  fun isPosted(id: String): Boolean {
    return postDao.isPosted(id)
  }

  fun getPostDetails(id: String): Post {
    return postDao.getPostDetails(id)
  }

  fun insertPost(post: Post) {
    postDao.insertPost(post)
  }

  fun deletePost(post: Post) {
    postDao.deletePost(post)
  }

  companion object {

    // For Singleton instantiation
    @Volatile
    private var instance: PostRepository? = null

    fun getInstance(postDao: PostDao) =
      instance
        ?: synchronized(this) {
          instance
            ?: PostRepository(
              postDao
            ).also { instance = it }
        }
  }
}