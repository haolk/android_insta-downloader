package com.lookie.instadownloader.utilities

import android.content.Context
import com.lookie.instadownloader.data.room.AppDatabase
import com.lookie.instadownloader.data.room.repository.PostRepository
import com.lookie.instadownloader.data.room.repository.UserRepository
import com.lookie.instadownloader.ui.download.PostListViewModelFactory
import com.lookie.instadownloader.ui.home.UserListViewModelFactory

/**
 * @author Phung Nguyen on 2020-01-11 22:02
 **/
object InjectorUtils {

  private fun getPostRepository(context: Context): PostRepository {
    return PostRepository.getInstance(AppDatabase.getInstance(context.applicationContext).postDao())
  }

  fun providePostListViewModelFactory(context: Context): PostListViewModelFactory {
    val repository = getPostRepository(context)
    return PostListViewModelFactory(repository)
  }

  private fun getUserRepository(context: Context): UserRepository {
    return UserRepository.getInstance(AppDatabase.getInstance(context.applicationContext).userDao())
  }

  fun provideUserListViewModelFactory(context: Context): UserListViewModelFactory {
    val repository = getUserRepository(context)
    return UserListViewModelFactory(repository)
  }
}