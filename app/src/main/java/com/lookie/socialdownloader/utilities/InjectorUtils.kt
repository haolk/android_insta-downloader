package com.lookie.socialdownloader.utilities

import android.content.Context
import com.lookie.socialdownloader.data.room.AppDatabase
import com.lookie.socialdownloader.data.room.repository.PostRepository
import com.lookie.socialdownloader.data.room.repository.UserRepository
import com.lookie.socialdownloader.ui.download.PostListViewModelFactory
import com.lookie.socialdownloader.ui.home.UserListViewModelFactory

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