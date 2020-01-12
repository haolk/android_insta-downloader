package com.lookie.socialdownloader.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lookie.socialdownloader.data.remote.model.OwnerModel
import com.lookie.socialdownloader.data.room.entity.User
import com.lookie.socialdownloader.data.room.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserListViewModel(private val userRepository: UserRepository) : ViewModel() {

  //CoroutineContext:
  private val completableJob = Job()
  private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

  val users: LiveData<List<User>> = userRepository.getUsers()

  fun insertUser(media: OwnerModel?) {
    val user = User(
      media!!.id!!,
      media.profilePicUrl!!,
      media.username!!,
      media.fullName!!,
      System.currentTimeMillis().toString()
    )
    coroutineScope.launch {
      if (!userRepository.isExists(user.id)) {
        userRepository.insertUser(user)
      }
    }
  }

  fun deleteUser(user: User?) {
    coroutineScope.launch {
      userRepository.deleteUser(user!!)
    }
  }
}