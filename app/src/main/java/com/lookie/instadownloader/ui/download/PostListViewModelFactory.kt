package com.lookie.instadownloader.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lookie.instadownloader.data.room.repository.PostRepository

/**
 * Factory for creating a [PostListViewModel] with a constructor that takes a
 * [PostRepository].
 */
class PostListViewModelFactory(
  private val repository: PostRepository
) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return PostListViewModel(repository) as T
  }
}