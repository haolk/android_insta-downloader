package com.lookie.socialdownloader.data.room.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lookie.socialdownloader.data.room.entity.Post

/**
 * @author Phung Nguyen on 2020-01-11 21:29
 **/
@Dao
interface PostDao {

  @Query("SELECT * FROM posts ORDER BY createAt DESC")
  fun getPosts(): LiveData<List<Post>>

  @Query("SELECT * FROM posts ORDER BY createAt DESC")
  fun getAllPosts(): DataSource.Factory<Int, Post>

  @Query("SELECT * FROM posts WHERE id = :id")
  fun getPostDetails(id: String): Post

  @Query("SELECT * FROM posts ORDER BY createAt DESC LIMIT 1")
  fun getLastPost(): LiveData<Post>

  @Query("SELECT EXISTS(SELECT 1 FROM posts WHERE id = :id LIMIT 1)")
  fun isPosted(id: String): Boolean

  @Insert
  fun insertPost(post: Post)

  @Delete
  fun deletePost(post: Post)
}