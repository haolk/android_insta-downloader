package com.lookie.socialdownloader.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lookie.socialdownloader.data.room.repository.PostRepository
import com.lookie.socialdownloader.data.room.repository.UserRepository

/**
 * Factory for creating a [PostListViewModel] with a constructor that takes a
 * [PostRepository].
 */
class UserListViewModelFactory(
  private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return UserListViewModel(repository) as T
  }
}